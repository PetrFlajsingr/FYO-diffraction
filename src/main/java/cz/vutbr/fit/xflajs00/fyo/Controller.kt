package cz.vutbr.fit.xflajs00.fyo

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.converter.NumberStringConverter


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


    @FXML
    fun initialize() {
        wavelengthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                wavelengthInput?.text = oldValue
            }
            wavelengthInput?.text = newValue.toInt().toString()
        }
        wavelengthInput?.textProperty()?.bindBidirectional(wavelengthSlider?.valueProperty(), NumberIntStringConverter())

        wavelengthSlider?.valueProperty()?.addListener { _ -> test() }

        projectDistInput?.textProperty()?.bindBidirectional(projectDistSlider?.valueProperty(), NumberStringConverter())
        projectDistSlider?.valueProperty()?.addListener { _ -> test() }

        intensityCanvas?.widthProperty()?.bind(intensityPane?.widthProperty())
        intensityCanvas?.heightProperty()?.bind(intensityPane?.heightProperty())

        intensityCanvas?.widthProperty()?.addListener { _ -> test() }
        intensityCanvas?.heightProperty()?.addListener { _ -> test() }

        slitCountInput?.valueProperty()?.addListener { _ -> test() }

        slitWidthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                slitWidthInput?.text = oldValue
                return@addListener
            }
            if (newValue.toDouble() >= slitDistInput!!.text.toDouble()) {
                slitWidthInput?.text = oldValue
            }
        }
        slitDistInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                slitDistInput?.text = oldValue
                return@addListener
            }
            if (newValue.toDouble() <= slitWidthInput!!.text.toDouble()) {
                slitDistInput?.text = oldValue
            }
        }
        slitWidthInput?.textProperty()?.addListener { _ -> test() }
        slitDistInput?.textProperty()?.addListener { _ -> test() }
        intensitySlider?.valueProperty()?.addListener { _ -> test() }

        graphCanvas?.widthProperty()?.bind(chartPane?.widthProperty())
        graphCanvas?.heightProperty()?.bind(chartPane?.heightProperty())

        diffTypeComboBox?.selectionModel?.select(0)
    }

    fun firstShow() {
        test()
    }

    private fun test() {
        val t = FraunhoferDiffraction()
        t.λ = wavelengthSlider!!.value * 1e-9
        t.D = projectDistSlider!!.value
        t.N = slitCountInput!!.value
        t.a = slitWidthInput!!.text.toDouble() * 10e-9
        t.b = slitDistInput!!.text.toDouble() * 10e-9
        val first = -t.π / 200
        val second = t.π / 200
        val step = (second - first) / 10000
        val tmp = t.calcInterval(first, second, step)

        drawIntensity(intensityCanvas!!, tmp, t.λ, intensitySlider!!.value)

        val t2 = FraunhoferDiffraction()
        t2.λ = wavelengthSlider!!.value * 1e-9
        t2.D = projectDistSlider!!.value
        t2.N = 1
        t2.a = slitWidthInput!!.text.toDouble() * 10e-9
        t2.b = slitDistInput!!.text.toDouble() * 10e-9
        val tmp2 = t2.calcInterval(first, second, step)

        val d = SimplePlotDrawer(graphCanvas!!)
        d.addValues(tmp, Color.RED)
        d.addValues(tmp2, Color.BLUE)
        d.draw()
    }

}