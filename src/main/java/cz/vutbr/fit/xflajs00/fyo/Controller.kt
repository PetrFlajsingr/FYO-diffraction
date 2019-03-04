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
    fun initialize() {
        wavelengthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                wavelengthInput?.text = oldValue
            }
            wavelengthInput?.text = newValue.toInt().toString()
        }
        wavelengthInput?.textProperty()?.bindBidirectional(wavelengthSlider?.valueProperty(), NumberIntStringConverter())

        intensityCanvas?.widthProperty()?.bind(intensityPane?.widthProperty())
        intensityCanvas?.heightProperty()?.bind(intensityPane?.heightProperty())
    }

    fun test() {
        val t = FraunhoferDiffraction()
        t.λ = 0.4e-6
        t.D = 1.0
        t.N = 10
        t.a = 5.0e-6
        t.b = 25.0e-6
        val first = -t.π / 200
        val second = t.π / 200
        val step = (second - first) / 2000
        val tmp = t.calcInterval(first, second, step)

        drawIntensity(intensityCanvas!!, tmp, t.λ, 20.0)

        val lineChartDataSet = DefaultCategoryDataset()
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
        chartCanvas.heightProperty().bind(chartPane?.heightProperty())
    }
}