package cz.vutbr.fit.xflajs00.fyo.drawing

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

/**
 * Clears canvas and draws provided data on it
 */
class SimplePlotDrawer(private val canvas: Canvas) {
    inner class ValueSet(val values: Array<Double>, val color: Color)
    inner class AxisInfo(val value: String, val pos: Double)

    private var values = mutableListOf<ValueSet>()
    private var xAxisText = mutableListOf<AxisInfo>()
    private var yAxisText = mutableListOf<AxisInfo>()

    private val xAxisInfoHeight = 30.0

    /**
     * Remove all saved datasets and axis data
     */
    fun clear() {
        values.clear()
        xAxisText.clear()
        yAxisText.clear()
    }

    /**
     *  Add row to draw
     */
    fun addValues(values: Array<Double>, color: Color) {
        this.values.add(ValueSet(values, color))
    }

    fun addXAxisText(value: String, pos: Double) {
        xAxisText.add(AxisInfo(value, pos))
    }

    fun addYAxisText(value: String, pos: Double) {
        yAxisText.add(AxisInfo(value, pos))
    }

    /**
     * Clear canvas and draw
     */
    fun draw() {
        canvas.graphicsContext2D.clearRect(0.0, 0.0, canvas.width, canvas.height)
        drawBackground()
        drawAxisInfo()
        drawValues()
    }

    private fun drawBackground() {
        // draw
    }

    private fun drawValues() {
        if (values.size == 0) {
            return
        }
        for (row in values) {
            val step = canvas.width / row.values.size
            var x = 0.0
            canvas.graphicsContext2D.fill = row.color
            canvas.graphicsContext2D.stroke = row.color

            val h = canvas.height - xAxisInfoHeight
            for (i in 0 until row.values.size - 1) {
                canvas.graphicsContext2D.strokeLine(x, h - h * row.values[i], x + step, h - h * row.values[i + 1])
                x += step
            }
        }
    }

    private fun drawAxisInfo() {
        canvas.graphicsContext2D.fill = Color.BLACK
        canvas.graphicsContext2D.stroke = Color.BLACK
        canvas.graphicsContext2D.strokeLine(0.0, canvas.height - xAxisInfoHeight, canvas.width, canvas.height - xAxisInfoHeight)
        for (info in xAxisText) {
            val pos = info.pos * canvas.width
            canvas.graphicsContext2D.fill = Color.BLACK
            canvas.graphicsContext2D.stroke = Color.BLACK
            canvas.graphicsContext2D.strokeText(info.value, pos, canvas.height - 10)

            canvas.graphicsContext2D.fill = Color.GRAY
            canvas.graphicsContext2D.stroke = Color.GRAY
            canvas.graphicsContext2D.strokeLine(pos, canvas.height - xAxisInfoHeight - 10, pos, canvas.height - xAxisInfoHeight + 10)
        }
    }
}