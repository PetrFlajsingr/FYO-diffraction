package cz.vutbr.fit.xflajs00.fyo

import javafx.scene.canvas.Canvas
import org.jfree.chart.JFreeChart
import org.jfree.fx.FXGraphics2D
import java.awt.geom.Rectangle2D

class ChartCanvas(private val chart: JFreeChart) : Canvas() {
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