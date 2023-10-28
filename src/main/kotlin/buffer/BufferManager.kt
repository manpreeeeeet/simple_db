package buffer

import filemanager.BlockId
import filemanager.FileManager
import log.LogManager

class BufferManager(val fileManager: FileManager, val logManager: LogManager, val numBuffers: Int) {

    fun pin(block: BlockId): Buffer {
        TODO()
    }

    fun unpin(buffer: Buffer) {
    }

    fun available(): Int {
        TODO()
    }

    fun flushAll(transactionNumber: Int) {
    }
}
