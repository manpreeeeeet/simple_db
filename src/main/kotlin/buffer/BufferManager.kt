package buffer

import exceptions.BufferAbortException
import filemanager.BlockId
import filemanager.FileManager
import log.LogManager

class BufferManager(val fileManager: FileManager, val logManager: LogManager, val numBuffers: Int) {

    private var numBuffersAvailable = numBuffers

    private val bufferPool = List(numBuffers) {
        Buffer(fileManager, logManager)
    }

    @Synchronized
    fun unpin(buffer: Buffer) {
        buffer.unpin()
        if (!buffer.isPinned) {
            numBuffersAvailable++
        }
    }

    @Synchronized
    fun available(): Int {
        return numBuffersAvailable
    }

    @Synchronized
    fun flushAll(transactionNumber: Int) {
        for (buff in bufferPool) {
            if (buff.transactionNumber == transactionNumber) {
                buff.flush()
            }
        }
    }

    @Synchronized
    fun pin(block: BlockId): Buffer {
        // find unpinned buffer or if requested block is already in buffer
        val startTime = System.currentTimeMillis()
        var buffer = tryToPin(block)

        try {
            while (buffer == null && !isWaitingTooLong(startTime)) {
                Thread.sleep(100L)
                buffer = tryToPin(block)
            }
        } catch (e: InterruptedException) {
            throw BufferAbortException(block)
        }

        return buffer!!
    }

    @Synchronized
    private fun tryToPin(block: BlockId): Buffer? {
        val buffer = getExistingBuffer(block) ?: getUnpinnedBuffer()

        if (buffer == null) {
            return buffer
        }
        buffer.assignToBlock(block)
        if (!buffer.isPinned) {
            numBuffersAvailable--
        }
        buffer.pin()

        return buffer
    }

    private fun getExistingBuffer(block: BlockId): Buffer? = bufferPool.firstOrNull { it.getBlock() == block }
    private fun getUnpinnedBuffer(): Buffer? = bufferPool.firstOrNull { !it.isPinned }

    private fun isWaitingTooLong(startTime: Long) =
        System.currentTimeMillis() - startTime > MAX_WAIT_TIME

    companion object {
        const val MAX_WAIT_TIME = 10000L // 10 seconds
    }
}
