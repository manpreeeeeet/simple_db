package exceptions

import filemanager.BlockId

class BufferAbortException(blockId: BlockId) :
    Exception("TimedOut while trying to allocate requested block: $blockId to a buffer.")
