package cz.vutbr.fit.xflajs00.fyo

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset


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
    private var slitCountInput: TextField? = null
    @FXML
    private var slitWidthInput: TextField? = null
    @FXML
    private var slitDistInput: TextField? = null
    @FXML
    private var projectDistSlider: Slider? = null


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
        projectDistSlider?.valueProperty()?.addListener { _ -> test() }

        intensityCanvas?.widthProperty()?.bind(intensityPane?.widthProperty())
        intensityCanvas?.heightProperty()?.bind(intensityPane?.heightProperty())

        test()
    }

    fun test() {
        val t = FraunhoferDiffraction()
        t.λ = wavelengthSlider!!.value * 1e-9
        t.D = projectDistSlider!!.value
        t.N = slitCountInput!!.text.toInt()
        t.a = slitWidthInput!!.text.toDouble() * 10e-9
        t.b = slitDistInput!!.text.toDouble() * 10e-9
        val first = -t.π / 200
        val second = t.π / 200
        val step = (second - first) / 10000
        val tmp = t.calcInterval(first, second, step)

        drawIntensity(intensityCanvas!!, tmp, t.λ, 20.0)

        /*val lineChartDataSet = DefaultCategoryDataset()
        var x = first
        for (i in 0 until tmp.size) {
            lineChartDataSet.addValue(tmp[i], "val", x)
            x += step
        }
        val chart = ChartFactory.createLineChart(
                "Test", "",
                "",
                lineChartDataSet, PlotOrientation.VERTICAL,
                false, false, false)

        val chartCanvas = ChartCanvas(chart)
        chartPane?.children?.clear()
        chartPane?.children?.add(chartCanvas)
        chartCanvas.widthProperty().bind(chartPane?.widthProperty())
        chartCanvas.heightProperty().bind(chartPane?.heightProperty())*/
    }

    fun graph() {
        val t = FraunhoferDiffraction()
        t.λ = wavelengthSlider!!.value * 1e-9//0.55e-6
        t.D = projectDistSlider!!.value
        t.N = slitCountInput!!.text.toInt()
        t.a = slitWidthInput!!.text.toDouble() * 10e-9
        t.b = slitDistInput!!.text.toDouble() * 10e-9
        val first = -t.π / 200
        val second = t.π / 200
        val step = (second - first) / 10000
        val tmp = t.calcInterval(first, second, step)

        val t2 = FraunhoferDiffraction()
        t2.λ = wavelengthSlider!!.value * 1e-9
        t2.D = projectDistSlider!!.value
        t2.N = 1
        t2.a = slitWidthInput!!.text.toDouble() * 10e-9
        t2.b = slitDistInput!!.text.toDouble() * 10e-9
        val tmp2 = t2.calcInterval(first, second, step)

        val lineChartDataSet = DefaultCategoryDataset()
        var x = first
        for (i in 0 until tmp.size) {
            lineChartDataSet.addValue(tmp[i], "val", x)
            lineChartDataSet.addValue(tmp2[i], "val2", x)
            x += step
        }
        val chart = ChartFactory.createLineChart(
                "Test", "",
                "",
                lineChartDataSet, PlotOrientation.VERTICAL,
                false, false, false)

        val chartCanvas = ChartCanvas(chart)
        chartPane?.children?.clear()
        chartPane?.children?.add(chartCanvas)
        chartCanvas.widthProperty().bind(chartPane?.widthProperty())
        chartCanvas.heightProperty().bind(chartPane?.heightProperty())
    }
}