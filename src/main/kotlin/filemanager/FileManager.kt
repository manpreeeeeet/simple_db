package filemanager

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class FileManager(private val dbDirectory: File, private val blockSize: Int) {

    private val openFiles = mutableMapOf<String, RandomAccessFile>()
    val isNew: Boolean = !dbDirectory.exists()

    init {

        if (isNew) {
            dbDirectory.mkdirs()
        }

        for (filename in dbDirectory.list()!!) {
            if (filename.startsWith("temp")) {
                File(dbDirectory, filename).delete()
            }
        }
    }

    fun read(blockId: BlockId, page: Page) {
        synchronized(this) {

            try {
                val file = getFile(blockId.filename)
                file.seek((blockId.blockNum * blockSize).toLong())
                file.channel.read(page.contents())
            } catch (e: IOException) {
                throw RuntimeException("Cannot read block $blockId")
            }

        }
    }


    fun write(blockId: BlockId, page: Page) {
        synchronized(this) {
            try {
                val file = getFile(blockId.filename)
                file.seek((blockId.blockNum * blockSize).toLong())
                file.channel.write(page.contents())

            } catch (e: IOException) {
                throw RuntimeException("Cannot write blocK $blockId")
            }
        }
    }

    fun append(fileName: String): BlockId {
        synchronized(this) {
            val length = length(fileName)
            val block = BlockId(fileName, length)

            val b = ByteArray(blockSize)
            try {
                val file = getFile(fileName)
                file.seek((block.blockNum * blockSize).toLong())
                file.write(b)
            } catch (e: IOException) {
                throw RuntimeException("Cannot append block $block")
            }

            return block
        }
    }

    fun length(fileName: String): Int {
        try {
            val file = getFile(fileName)
            return (file.length() / blockSize).toInt()
        } catch (e: IOException) {
            throw RuntimeException("Cannot access file $fileName")
        }
    }

    private fun getFile(fileName: String): RandomAccessFile {
        var file = openFiles[fileName]

        if (file == null) {
            val dbTable = File(dbDirectory, fileName)
            file = RandomAccessFile(dbTable, "rws")
            openFiles[fileName] = file
        }

        return file
    }

}

fun main() {
    val file = File("abc")

    val fileManager = FileManager(file, 4096)

    val block = fileManager.append("a.dat")

    val page = Page(4096)
    page.setInt(0, 999)
    page.setInt(0 + Int.SIZE_BYTES, 888)
    fileManager.write(block, page)

    val page1 = Page(4096)


    fileManager.read(block, page)


}



















