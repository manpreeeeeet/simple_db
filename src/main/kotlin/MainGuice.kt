import com.google.inject.AbstractModule
import com.google.inject.Guice
import filemanager.FileManager
import filemanager.FileManagerLogic
import filemanager.IFileManagerLogic
import filemanager.Page
import java.io.File

class MainGuice : AbstractModule() {
    override fun configure() {
        bind(IFileManagerLogic::class.java).to(FileManagerLogic::class.java)
    }
}

fun main() {
    val injector = Guice.createInjector(MainGuice())


    val file = File("abc")

    val fileManager = FileManager(file, 4096)
    injector.injectMembers(fileManager)

    val block = fileManager.append("a.dat")

    val page = Page(4096)
    page.setInt(0, 999)
    page.setInt(0 + Int.SIZE_BYTES, 888)
    fileManager.write(block, page)

    val page1 = Page(4096)


    fileManager.read(block, page)


}