import com.google.inject.Guice
import filemanager.FileManager
import filemanager.Page
import log.LogManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class LogIteratorTest {

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
    fun `records that are multi page can be iterated`() {
        val lm = LogManager(fm, logFile)
        val logRecords = List(4096) {
            "hi i am $it number"
        }
        for (record in logRecords) {
            lm.append(record.toByteArray(Page.CHARSET))
        }

        val recordsFound = mutableListOf<String>()
        for (record in lm.getLogIterator()) {
            recordsFound.add(record.toString(Page.CHARSET))
        }

        assertEquals(logRecords.reversed(), recordsFound)
    }
}
