package buffer

import filemanager.BlockId
import filemanager.FileManager
import filemanager.Page
import log.LogManager

/**
 * Represents a buffer, that contains a [Page] and other data such as
 * whether the [Page] is pinned or not.
 */
class Buffer(private val fileManager: FileManager, private val logManager: LogManager) {

    val contents = Page(fileManager.blockSize)

    private var pins = 0

    private var block: BlockId? = null

    var transactionNumber = -1

    private var lsn = -1

    fun isPinned() = pins > 0

    fun setModified(transactionNumber: Int, lsn: Int) {
        this.transactionNumber = transactionNumber
        if (lsn >= 0) {
            this.lsn = lsn
        }
    }

    /**
     * Flushes the previously assigned block if dirty,
     * and assigns a new block
     */
    fun assignToBlock(blockId: BlockId) {
        flush()
        block = blockId
        fileManager.read(blockId, contents)
        pins = 0
    }

    private fun flush() {
        if (transactionNumber >= 0) {
            logManager.flush(lsn)
            fileManager.write(block!!, contents)
            transactionNumber--
            lsn--
        }
    }
}
