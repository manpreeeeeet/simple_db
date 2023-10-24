package filemanager

import filemanager.BlockData.BLOCK_SIZE
import java.io.IOException
import java.io.RandomAccessFile

class FileManagerLogic : IFileManagerLogic {
    override fun read(file: RandomAccessFile, blockId: BlockId, page: Page) {
        synchronized(this) {
            try {
                file.seek((blockId.blockNum * BLOCK_SIZE).toLong())
                file.channel.read(page.contents())
            } catch (e: IOException) {
                throw RuntimeException("Cannot read block $blockId")
            }

        }
    }

    override fun write(file: RandomAccessFile, blockId: BlockId, page: Page) {
        synchronized(this) {
            try {

                file.seek((blockId.blockNum * BLOCK_SIZE).toLong())
                file.channel.write(page.contents())

            } catch (e: IOException) {
                throw RuntimeException("Cannot write blocK $blockId")
            }
        }
    }

    override fun append(file: RandomAccessFile, fileName: String): BlockId {
        synchronized(this) {

            val length = length(file, fileName)
            val block = BlockId(fileName, length)

            val b = ByteArray(BLOCK_SIZE)
            try {
                file.seek((block.blockNum * BLOCK_SIZE).toLong())
                file.write(b)
            } catch (e: IOException) {
                throw RuntimeException("Cannot append block $block")
            }

            return block
        }
    }

    companion object {
        fun length(file: RandomAccessFile, fileName: String): Int {
            try {
                return (file.length() / BLOCK_SIZE).toInt()
            } catch (e: IOException) {
                throw RuntimeException("Cannot access file $fileName")
            }
        }
    }
}