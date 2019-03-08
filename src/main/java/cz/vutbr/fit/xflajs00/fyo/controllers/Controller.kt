package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.FraunhoferDiffraction
import cz.vutbr.fit.xflajs00.fyo.NumberIntStringConverter
import cz.vutbr.fit.xflajs00.fyo.drawing.Intensity
import cz.vutbr.fit.xflajs00.fyo.drawing.SimplePlotDrawer
import cz.vutbr.fit.xflajs00.fyo.drawing.drawCombinedIntensity
import cz.vutbr.fit.xflajs00.fyo.drawing.drawIntensity
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.converter.NumberStringConverter
import kotlin.math.PI


class Controller {
    @FXML
    private var chartPane: Pane? = null
    @FXML
    private var wavelengthInput: TextField? = null
    @FXML
    private var wavelengthSlider: Slider? = null
    @FXML
    private var intensityPane: Pane? = null
    @FXML
    private var intensityCanvas: Canvas? = null
    @FXML
    private var slitCountInput: Spinner<Int>? = null
    @FXML
    private var slitWidthInput: TextField? = null
    @FXML
    private var slitDistInput: TextField? = null
    @FXML
    private var projectDistInput: TextField? = null
    @FXML
    private var projectDistSlider: Slider? = null
    @FXML
    private var intensitySlider: Slider? = null
    @FXML
    private var graphCanvas: Canvas? = null
    @FXML
    private var diffTypeComboBox: ComboBox<String>? = null

    private val fraunhoferDiffraction = FraunhoferDiffraction()
    private var intensity: Intensity? = null
    private var intensityN1: Intensity? = null


    @FXML
    fun initialize() {
        wavelengthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                wavelengthInput?.text = oldValue
            }
            wavelengthInput?.text = newValue.toInt().toString()
        }
        wavelengthInput?.textProperty()?.bindBidirectional(wavelengthSlider?.valueProperty(), NumberIntStringConverter())

        wavelengthSlider?.valueProperty()?.addListener { _ -> drawDiffraction() }

        projectDistInput?.textProperty()?.bindBidirectional(projectDistSlider?.valueProperty(), NumberStringConverter())
        projectDistSlider?.valueProperty()?.addListener { _ -> drawDiffraction() }

        intensityCanvas?.widthProperty()?.bind(intensityPane?.widthProperty())
        intensityCanvas?.heightProperty()?.bind(intensityPane?.heightProperty())

        intensityCanvas?.widthProperty()?.addListener { _ -> drawDiffraction() }
        intensityCanvas?.heightProperty()?.addListener { _ -> drawDiffraction() }

        slitCountInput?.valueProperty()?.addListener { _ -> drawDiffraction() }

        slitWidthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                slitWidthInput?.text = oldValue
                return@addListener
            }
            if (newValue.toDouble() >= slitDistInput!!.text.toDouble()) {
                slitDistInput?.text = (newValue.toDouble() + 1).toString()
            }
        }
        slitDistInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                slitDistInput?.text = oldValue
                return@addListener
            }
            if (newValue.toDouble() <= slitWidthInput!!.text.toDouble()) {
                slitWidthInput?.text = (newValue.toDouble() - 1).toString()
            }
        }
        slitWidthInput?.textProperty()?.addListener { _ -> drawDiffraction() }
        slitDistInput?.textProperty()?.addListener { _ -> drawDiffraction() }
        intensitySlider?.valueProperty()?.addListener { _ -> drawDiffraction() }

        graphCanvas?.widthProperty()?.bind(chartPane?.widthProperty())
        graphCanvas?.heightProperty()?.bind(chartPane?.heightProperty())

        diffTypeComboBox?.selectionModel?.select(0)
    }

    fun openSettings() {
        val loader = FXMLLoader()
        loader.location = javaClass.classLoader.getResource("settings.fxml")
        val root = loader.load<Parent>()
        val stage = Stage()
        stage.title = "Settings"
        stage.scene = Scene(root)
        stage.scene.stylesheets.add(javaClass.classLoader.getResource("style.css").toString())
        stage.setOnCloseRequest { loader.getController<SettingsController>().onClose() }
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.showAndWait()
    }

    fun firstShow() {
        drawDiffraction()
    }

    private infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
        require(start.isFinite())
        require(endInclusive.isFinite())
        require(step > 0.0) { "Step must be positive, was: $step." }
        val sequence = generateSequence(start) { previous ->
            if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
            val next = previous + step
            if (next > endInclusive) null else next
        }
        return sequence.asIterable()
    }

    private fun drawCombinedDiffraction() {
        val vals = mutableListOf<Intensity>()
        for (λ in 380.0..750.0 step 5.0) {
            fraunhoferDiffraction.λ = λ * 1e-9
            fraunhoferDiffraction.D = projectDistSlider!!.value
            fraunhoferDiffraction.N = slitCountInput!!.value
            fraunhoferDiffraction.a = slitWidthInput!!.text.toDouble() * 10e-9
            fraunhoferDiffraction.b = slitDistInput!!.text.toDouble() * 10e-9
            val first = -PI / 4
            val second = PI / 4
            val step = (second - first) / 7500
            vals.add(Intensity(λ * 1e-9, fraunhoferDiffraction.calcInterval(first, second, step)))
        }
        drawCombinedIntensity(intensityCanvas!!, vals, intensitySlider!!.value)

        val first = -PI / 4 // / 200
        val second = PI / 4 // / 200
        val step = (second - first) / 30000
        val t2 = FraunhoferDiffraction()
        t2.λ = wavelengthSlider!!.value * 1e-9
        t2.D = projectDistSlider!!.value
        t2.N = 1
        t2.a = slitWidthInput!!.text.toDouble() * 10e-9
        t2.b = slitDistInput!!.text.toDouble() * 10e-9
        val tmp2 = t2.calcInterval(first, second, step)

        val d = SimplePlotDrawer(graphCanvas!!)
        d.addValues(tmp2, Color.BLUE)
        d.addXAxisText("0°", 0.5)
        d.draw()
    }

    private fun drawDiffraction() {
        /*if (whiteLightCheckbox!!.isSelected) {
            drawCombinedDiffraction()
            return
        }*/

        fraunhoferDiffraction.λ = wavelengthSlider!!.value * 1e-9
        fraunhoferDiffraction.D = projectDistSlider!!.value
        fraunhoferDiffraction.N = slitCountInput!!.value
        fraunhoferDiffraction.a = slitWidthInput!!.text.toDouble() * 10e-9
        fraunhoferDiffraction.b = slitDistInput!!.text.toDouble() * 10e-9
        val first = -PI / 4 // / 200
        val second = PI / 4 // / 200
        val step = (second - first) / 30000

        intensity = Intensity(fraunhoferDiffraction.λ, fraunhoferDiffraction.calcInterval(first, second, step))
        drawIntensity(intensityCanvas!!, intensity!!, intensitySlider!!.value)

        val t2 = FraunhoferDiffraction()
        t2.λ = wavelengthSlider!!.value * 1e-9
        t2.D = projectDistSlider!!.value
        t2.N = 1
        t2.a = slitWidthInput!!.text.toDouble() * 10e-9
        t2.b = slitDistInput!!.text.toDouble() * 10e-9
        intensityN1 = Intensity(fraunhoferDiffraction.λ, t2.calcInterval(first, second, step))

        val d = SimplePlotDrawer(graphCanvas!!)
        d.addValues(intensity!!.intensities, Color.RED)
        d.addValues(intensityN1!!.intensities, Color.BLUE)
        d.addXAxisText("0°", 0.5)
        d.draw()
    }


    fun saveDiffractionPattern() {
        val loader = FXMLLoader()
        loader.location = javaClass.classLoader.getResource("resolution_choice.fxml")
        val controller = ResolutionChoiceController(true, intensitySlider!!.value)
        controller.intensity = intensity
        loader.setController(controller)
        val root = loader.load<Parent>()
        val stage = Stage()
        stage.title = "Save diffraction pattern"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.showAndWait()
    }

    fun saveGraph() {
        val loader = FXMLLoader()
        loader.location = javaClass.classLoader.getResource("resolution_choice.fxml")
        val controller = ResolutionChoiceController(false, intensitySlider!!.value)
        controller.plotIntensity1 = intensity
        controller.plotIntensity2 = intensityN1
        loader.setController(controller)
        val root = loader.load<Parent>()
        val stage = Stage()
        stage.title = "Save diffraction pattern"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.showAndWait()
    }

}