package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.FraunhoferDiffraction
import cz.vutbr.fit.xflajs00.fyo.NumberIntStringConverter
import cz.vutbr.fit.xflajs00.fyo.drawing.*
import cz.vutbr.fit.xflajs00.fyo.models.ConfigModel
import cz.vutbr.fit.xflajs00.fyo.models.LightSourceModel
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
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
    private var lightSourceChoice: ChoiceBox<String>? = null
    @FXML
    private var colorRadioButton: RadioButton? = null
    @FXML
    private var intensityRadioButton: RadioButton? = null
    private val radioGroup = ToggleGroup()

    private val fraunhoferDiffraction = FraunhoferDiffraction()
    private var intensity: Intensity? = null
    private var intensityN1: Intensity? = null

    private var lightSources: List<LightSourceModel> = emptyList()

    private var combinedWavelength = false

    private var monoStep: Int = 15000
    private var combStep: Int = 7500
    private var combWaveStep: Double = 5.0

    init {
        setConfigValues()
    }

    fun setConfigValues() {
        val configs = ConfigModel.loadFromConfig()
        for (config in configs) {
            when (config.getRawKey()) {
                "mono_step" -> monoStep = config.getValue().toInt()
                "comb_step" -> combStep = config.getValue().toInt()
                "comb_wave_step" -> combWaveStep = config.getValue().toDouble()
            }
        }
    }

    @FXML
    fun initialize() {
        colorRadioButton?.toggleGroup = radioGroup
        intensityRadioButton?.toggleGroup = radioGroup
        radioGroup.selectedToggleProperty().addListener { _ ->
            drawDiffraction()
        }
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

        lightSources = LightSourceModel.loadFromConfig()

        lightSourceChoice?.items?.add("Monochromatic")
        lightSourceChoice?.selectionModel?.select(0)
        lightSourceChoice?.selectionModel?.selectedIndexProperty()?.addListener { _, _, newValue ->
            if (newValue != 0) {
                wavelengthSlider?.isDisable = true
                wavelengthInput?.isDisable = true
                combinedWavelength = true
            } else {
                wavelengthSlider?.isDisable = false
                wavelengthInput?.isDisable = false
                combinedWavelength = false
            }
            drawDiffraction()
        }

        for (source in lightSources) {
            lightSourceChoice?.items?.add(source.name)
        }
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
        setConfigValues()
        drawDiffraction()
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
        val selectedLightSource = lightSources[lightSourceChoice!!.selectionModel.selectedIndex - 1]

        val vals = mutableListOf<Intensity>()
        for (λ in selectedLightSource.waveLengths) {
            fraunhoferDiffraction.λ = λ * 1e-9
            fraunhoferDiffraction.D = projectDistSlider!!.value
            fraunhoferDiffraction.N = slitCountInput!!.value
            fraunhoferDiffraction.a = slitWidthInput!!.text.toDouble() * 10e-9
            fraunhoferDiffraction.b = slitDistInput!!.text.toDouble() * 10e-9
            val first = -PI / 4
            val second = PI / 4
            val step = (second - first) / combStep
            vals.add(Intensity(λ * 1e-9, fraunhoferDiffraction.calcInterval(first, second, step)))
        }
        for (intensityInterval in selectedLightSource.waveLengthIntervals) {
            for (λ in intensityInterval.first..intensityInterval.second step combWaveStep) {
                fraunhoferDiffraction.λ = λ * 1e-9
                fraunhoferDiffraction.D = projectDistSlider!!.value
                fraunhoferDiffraction.N = slitCountInput!!.value
                fraunhoferDiffraction.a = slitWidthInput!!.text.toDouble() * 10e-9
                fraunhoferDiffraction.b = slitDistInput!!.text.toDouble() * 10e-9
                val first = -PI / 4
                val second = PI / 4
                val step = (second - first) / combStep
                vals.add(Intensity(λ * 1e-9, fraunhoferDiffraction.calcInterval(first, second, step)))
            }
        }
        drawCombinedIntensity(intensityCanvas!!, vals, intensitySlider!!.value, colorRadioButton!!.isSelected)

        val d = SimplePlotDrawer(graphCanvas!!)
        for (value in vals) {
            val rgb = waveLengthToRGB(value.waveLength)
            d.addValues(value.intensities, Color(rgb[0], rgb[1], rgb[2], 1.0))
        }
        d.addXAxisText("0°", 0.5)
        d.draw()
    }

    private fun drawDiffraction() {
        if (combinedWavelength) {
            drawCombinedDiffraction()
            return
        }

        fraunhoferDiffraction.λ = wavelengthSlider!!.value * 1e-9
        fraunhoferDiffraction.D = projectDistSlider!!.value
        fraunhoferDiffraction.N = slitCountInput!!.value
        fraunhoferDiffraction.a = slitWidthInput!!.text.toDouble() * 10e-9
        fraunhoferDiffraction.b = slitDistInput!!.text.toDouble() * 10e-9
        val first = -PI / 4
        val second = PI / 4
        val step = (second - first) / monoStep

        intensity = Intensity(fraunhoferDiffraction.λ, fraunhoferDiffraction.calcInterval(first, second, step))
        drawIntensity(intensityCanvas!!, intensity!!, intensitySlider!!.value, colorRadioButton!!.isSelected)

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