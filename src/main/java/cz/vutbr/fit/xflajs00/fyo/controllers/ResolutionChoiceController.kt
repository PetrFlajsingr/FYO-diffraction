package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.drawing.Intensity
import cz.vutbr.fit.xflajs00.fyo.drawing.SimplePlotDrawer
import cz.vutbr.fit.xflajs00.fyo.drawing.drawIntensity
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.IOException
import javax.imageio.ImageIO


class ResolutionChoiceController(private val pattern: Boolean, private val scale: Double) {
    @FXML
    private var resolutionXInput: TextField? = null
    @FXML
    private var resolutionYInput: TextField? = null
    @FXML
    private var formatChoice: ChoiceBox<String>? = null

    var intensity: Intensity? = null
    var intensities: List<Intensity>? = null

    var plotIntensity1: Intensity? = null
    var plotIntensity2: Intensity? = null


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
                val canvas = Canvas(resolutionXInput!!.text.toDouble(), resolutionYInput!!.text.toDouble())
                if (pattern) {
                    drawIntensity(canvas, intensity!!, scale)
                } else {
                    val plotDrawer = SimplePlotDrawer(canvas)
                    plotDrawer.addValues(plotIntensity1!!.intensities, Color.RED)
                    plotDrawer.addValues(plotIntensity2!!.intensities, Color.BLUE)
                    plotDrawer.draw()
                }
                val image = canvas.snapshot(null, null)
                val img = SwingFXUtils.fromFXImage(image, null)
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
