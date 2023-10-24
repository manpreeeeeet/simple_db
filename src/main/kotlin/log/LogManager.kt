package log

import filemanager.BlockId
import filemanager.FileManager
import filemanager.Page

class LogManager(private val fileManager: FileManager, private val logFile: String) {

    private val logPage: Page = Page(fileManager.blockSize)
    private var currentBlock: BlockId
    private var latestLSN = 0
    private var lastSavedLSN = 0

    init {
        val length = fileManager.length(logFile)

        currentBlock = if (length == 0) {
            appendNewBlock()
        } else {
            val block = BlockId(logFile, length - 1)
            fileManager.read(block, logPage)
            block
        }
    }

    fun getLogIterator(): LogIterator {
        flush()
        return LogIterator(fileManager, currentBlock)
    }

    fun flush(lsn: Int) {
        if (lsn >= lastSavedLSN) {
            flush()
        }
    }

    fun append(byteArray: ByteArray): Int {
        synchronized(this) {
            val recordSize = byteArray.size
            var boundary = logPage.getInt(0)

            val bytesNeeded = recordSize + Int.SIZE_BYTES
            if (boundary - bytesNeeded < Int.SIZE_BYTES) {
                flush()
                currentBlock = appendNewBlock()
                boundary = logPage.getInt(0)
            }

            val recordPosition = boundary - bytesNeeded
            logPage.setBytes(recordPosition, byteArray)
            logPage.setInt(0, recordPosition)
            latestLSN += 1

            return latestLSN
        }
    }

    private fun appendNewBlock(): BlockId {
        val block = fileManager.append(logFile)
        logPage.setInt(0, fileManager.blockSize)
        fileManager.write(block, logPage)
        return block
    }

    private fun flush() {
        fileManager.write(currentBlock, logPage)
        lastSavedLSN = latestLSN
    }
}
