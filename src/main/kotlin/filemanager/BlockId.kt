package filemanager

data class BlockId(val filename: String, var blockNum: Int)

object BlockData {
    const val BLOCK_SIZE = 4096
}
