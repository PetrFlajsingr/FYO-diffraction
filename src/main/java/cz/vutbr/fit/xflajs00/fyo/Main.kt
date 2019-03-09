package cz.vutbr.fit.xflajs00.fyo

import cz.vutbr.fit.xflajs00.fyo.controllers.Controller
import cz.vutbr.fit.xflajs00.fyo.models.ConfigModel
import cz.vutbr.fit.xflajs00.fyo.models.stripFile
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

// TODO custom color config
// angles in graph
// translate to english
//

fun main() {
    if (!File(getJarLocation() + "\\settings.conf").exists()) {
        prepareConfFiles()
    }
    Application.launch(Main::class.java)
}

fun prepareConfFiles() {
    val lightSources = Main::class.java.classLoader.getResourceAsStream("light_sources.conf")
    val settings = Main::class.java.classLoader.getResourceAsStream("settings.conf")

    val jarLocation = getJarLocation()

    val settingsFile = File("$jarLocation\\settings.conf")
    settingsFile.createNewFile()
    settingsFile.writeBytes(settings.readBytes())
    val lightFile = File("$jarLocation\\light_sources.conf")
    lightFile.createNewFile()
    lightFile.writeBytes(lightSources.readBytes())
}

fun getJarLocation(): String {
    val jarLocationUrl = ConfigModel::class.java.protectionDomain.codeSource.location
   return stripFile(File(jarLocationUrl.toString()).parent)
}

class Main : javafx.application.Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage?) {
        val loader = FXMLLoader()
        loader.location = javaClass.classLoader.getResource("main_window.fxml")
        val controller = Controller()
        loader.setController(controller)
        val root = loader.load<Parent>()
        primaryStage?.title = "FYO diffraction"
        primaryStage?.scene = Scene(root)
        primaryStage?.show()
        controller.firstShow()
    }
}
