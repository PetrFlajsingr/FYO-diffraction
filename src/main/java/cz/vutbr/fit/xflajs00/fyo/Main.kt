package cz.vutbr.fit.xflajs00.fyo

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


fun main() {
    Application.launch(Main::class.java)
}

class Main : javafx.application.Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage?) {
        val loader = FXMLLoader()
        loader.location = javaClass.classLoader.getResource("sample.fxml")
        val controller = Controller()
        loader.setController(controller)
        val root = loader.load<Parent>()
        primaryStage?.title = "FYO diffraction"
        primaryStage?.scene = Scene(root)
        primaryStage?.show()
        controller.firstShow()
    }
}
