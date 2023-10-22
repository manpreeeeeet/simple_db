package filemanager

import com.google.inject.Inject
import java.io.File
import java.io.RandomAccessFile

class FileManager(private val dbDirectory: File, private val blockSize: Int) {

    @Inject
    lateinit var fileManagerLogic: IFileManagerLogic

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
        fileManagerLogic.read(getFile(blockId.filename), blockId, page)
    }


    fun write(blockId: BlockId, page: Page) {
        fileManagerLogic.write(getFile(blockId.filename), blockId, page)
    }

    fun append(fileName: String): BlockId {
        return fileManagerLogic.append(getFile(fileName), fileName)
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




















