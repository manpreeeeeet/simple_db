package log

import filemanager.BlockId
import filemanager.FileManager
import filemanager.Page

class LogIterator(val fileManager: FileManager, block: BlockId) : Iterator<ByteArray> {

    private val currentLogPage: Page = Page(fileManager.blockSize)
    private var currentBoundary: Int = 0
    private var currentPosition: Int = 0
    private var currentBlock = block

    init {
        moveToBlock(block)
    }

    override fun hasNext(): Boolean {
        return currentPosition < fileManager.blockSize || currentBlock.blockNum > 0
    }

    override fun next(): ByteArray {
        if (currentPosition == fileManager.blockSize) {
            moveToBlock(currentBlock.apply { blockNum -= 1 })
        }
        return currentLogPage.getBytes(currentPosition).also {
            currentPosition += it.size + Int.SIZE_BYTES
        }
    }

    private fun moveToBlock(block: BlockId) {
        fileManager.read(block, currentLogPage)
        currentBoundary = currentLogPage.getInt(0)
        currentPosition = currentBoundary
    }

}