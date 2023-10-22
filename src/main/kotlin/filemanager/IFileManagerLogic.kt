package filemanager

import java.io.RandomAccessFile

interface IFileManagerLogic {
    fun read(file: RandomAccessFile, blockId: BlockId, page: Page)
    fun write(file: RandomAccessFile, blockId: BlockId, page: Page)
    fun append(file: RandomAccessFile, fileName: String): BlockId

}