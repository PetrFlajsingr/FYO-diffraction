package cz.vutbr.fit.xflajs00.fyo

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
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
        stackPane?.children?.add(chartCanvas)
        chartCanvas.widthProperty().bind(stackPane?.widthProperty())
        chartCanvas.heightProperty().bind(stackPane?.heightProperty())
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