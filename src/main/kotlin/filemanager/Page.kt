package filemanager

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Page(private val buffer: ByteBuffer) {

    constructor(blockSize: Int) : this(ByteBuffer.allocateDirect(blockSize))

    companion object {
        val CHARSET: Charset = StandardCharsets.UTF_8

        fun maxLength(strLength: Int): Int {
            val bytesPerChar = CHARSET.newEncoder().maxBytesPerChar()
            return Int.SIZE_BYTES + (strLength * (bytesPerChar.toInt()))
        }
    }

    fun getInt(offset: Int): Int {
        return buffer.getInt(offset)
    }

    fun setInt(offset: Int, n: Int) {
        buffer.putInt(offset, n)
    }

    fun getBytes(offset: Int): ByteArray {
        buffer.position(offset)
        val length = buffer.getInt()
        return ByteArray(length).also {
            buffer.get(it)
        }
    }

    fun setBytes(offset: Int, b: ByteArray) {
        buffer.position(offset)
        buffer.putInt(b.size)
        buffer.put(b)
    }

    fun getString(offset: Int): String {
        return String(getBytes(offset), CHARSET)
    }

    fun setString(offset: Int, s: String) {
        setBytes(offset, s.toByteArray(CHARSET))
    }

    fun contents(): ByteBuffer {
        buffer.position(0)
        return buffer
    }
}
