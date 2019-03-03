package cz.vutbr.fit.xflajs00.fyo

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.data.time.Day
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.fx.FXGraphics2D
import java.awt.geom.Rectangle2D

class Controller {
    @FXML
    private var stackPane: StackPane? = null
    @FXML
    private var chartPane: Pane? = null
    @FXML
    private var confVBox: VBox? = null
    @FXML
    private var wavelengthInput: TextField? = null
    @FXML
    private var wavelengthSlider: Slider? = null


    @FXML
    fun initialize() {
        val t1 = TimeSeries("test man")
        t1.add(Day(1, 1, 2000), 10.8)
        t1.add(Day(2, 1, 2000), 11.8)
        t1.add(Day(3, 1, 2000), 12.8)
        t1.add(Day(4, 1, 2000), 13.8)
        val dataset = TimeSeriesCollection()
        dataset.addSeries(t1)
        val chart = ChartFactory.createTimeSeriesChart("test", null, null, dataset)
        val chartCanvas = ChartCanvas(chart)
        chartPane?.children?.add(chartCanvas)
        chartCanvas.widthProperty().bind(chartPane?.widthProperty())
        chartCanvas.heightProperty().bind(chartPane?.heightProperty())

        wavelengthInput?.textProperty()?.addListener { _, oldValue, newValue ->
            if (newValue.toDoubleOrNull() == null) {
                wavelengthInput?.text = oldValue
            }
            wavelengthInput?.text = newValue.toInt().toString()
        }
        wavelengthInput?.textProperty()?.bindBidirectional(wavelengthSlider?.valueProperty(), CustomIntStringConverter())
    }

    inner class CustomIntStringConverter : NumberStringConverter() {

        override fun toString(value: Number?): String {
            return value?.toInt().toString()
        }

        override fun fromString(value: String?): Number {
            return value?.toIntOrNull() ?: return 0
        }
    }

    inner class ChartCanvas(private val chart: JFreeChart) : Canvas() {
        private val g2: FXGraphics2D = FXGraphics2D(graphicsContext2D)

        init {
            widthProperty().addListener { _ -> draw() }
            heightProperty().addListener { _ -> draw() }
        }

        private fun draw() {
            graphicsContext2D.clearRect(0.0, 0.0, width, height)
            chart.draw(g2, Rectangle2D.Double(0.0, 0.0, width, height))
        }

        override fun isResizable(): Boolean {
            return true
        }

        override fun prefWidth(height: Double): Double {
            return width
        }

        override fun prefHeight(width: Double): Double {
            return height
        }
    }
}