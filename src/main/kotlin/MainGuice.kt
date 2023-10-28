import com.google.inject.AbstractModule
import filemanager.FileManagerLogic
import filemanager.IFileManagerLogic

class MainGuice : AbstractModule() {
    override fun configure() {
        bind(IFileManagerLogic::class.java).to(FileManagerLogic::class.java)
    }
}
