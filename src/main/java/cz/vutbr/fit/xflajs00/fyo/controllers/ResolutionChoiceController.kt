package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.drawing.Intensity
import cz.vutbr.fit.xflajs00.fyo.drawing.Resolution
import cz.vutbr.fit.xflajs00.fyo.drawing.drawIntensity
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.IOException
import javax.imageio.ImageIO


class ResolutionChoiceController(private val pattern: Boolean, private val intensity: Intensity, private val scale: Double) {
    @FXML
    private var resolutionXInput: TextField? = null
    @FXML
    private var resolutionYInput: TextField? = null
    @FXML
    private var formatChoice: ChoiceBox<String>? = null

    @FXML
    fun initialize() {
        resolutionXInput?.textProperty()?.addListener { observable, oldValue, newValue ->
            if (!newValue.matches("\\d*".toRegex())) {
                resolutionXInput?.text = newValue.replace("[^\\d]".toRegex(), "")
            }
        }

        resolutionYInput?.textProperty()?.addListener { observable, oldValue, newValue ->
            if (!newValue.matches("\\d*".toRegex())) {
                resolutionYInput?.text = newValue.replace("[^\\d]".toRegex(), "")
            }
        }

        formatChoice?.selectionModel?.selectFirst()
    }

    fun saveImage() {
        val fileChooser = FileChooser()
        fileChooser.title = "Save image"
        val file = fileChooser.showSaveDialog((resolutionXInput!!.scene.window as Stage))
        if (file != null) {
            try {
                val img = drawIntensity(Resolution(resolutionXInput!!.text.toInt(), resolutionYInput!!.text.toInt()), intensity, scale)
                ImageIO.write(img, formatChoice!!.value, file)
            } catch (e: IOException) {
            }

            close()
        }

    }

    fun close() {
        (resolutionXInput!!.scene.window as Stage).close()
    }
}
