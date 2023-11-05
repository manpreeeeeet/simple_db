import com.google.inject.Guice
import filemanager.BlockId
import filemanager.FileManager
import filemanager.Page
import log.LogManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class LogManagerTest {

    val testDirectory = "test_dir"
    val logFile = "temp_log_file"

    lateinit var fm: FileManager

    @BeforeEach
    fun setup() {
        val injector = Guice.createInjector(MainGuice())
        fm = FileManager(File(testDirectory), 4096)
        injector.injectMembers(fm)
    }

    @AfterEach
    fun cleanup() {
        File(testDirectory, logFile).delete()
        File(testDirectory).delete()
    }

    @Test
    fun `log append is successful`() {
        val sut = LogManager(fm, logFile)

        val logString = "hi this is a test log"
        val lsn = sut.append(logString.toByteArray(Page.CHARSET))
        sut.flush(lsn)

        val page = Page(fm.blockSize)
        fm.read(
            BlockId(logFile, fm.length(logFile) - 1),
            page
        )

        val boundary = page.getInt(0)
        val lastWrittenLog = page.getBytes(boundary).toString(Page.CHARSET)

        assertEquals(logString, lastWrittenLog)
    }

    @Test
    fun `log gets auto flushed when page is full`() {
        val sut = LogManager(fm, logFile)

        val logString = "hello i am string: "
        val maxStringsOnPage =
            (fm.blockSize - Int.SIZE_BYTES) / ((logString + 100.toString()).toByteArray(Page.CHARSET).size + Int.SIZE_BYTES)

        repeat(maxStringsOnPage + 1) {
            sut.append((logString + (100 + it).toString()).toByteArray(Page.CHARSET))
        }

        // second last block on page should have logs written to disk
        val page = Page(fm.blockSize)
        fm.read(
            BlockId(logFile, fm.length(logFile) - 2), // -2 because logManager appends a new block after flushing
            page
        )

        assertEquals(logString + "256", page.getString(page.getInt(0)))
    }
}
