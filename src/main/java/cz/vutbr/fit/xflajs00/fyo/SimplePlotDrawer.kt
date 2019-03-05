package cz.vutbr.fit.xflajs00.fyo

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

class SimplePlotDrawer(val canvas: Canvas) {
    inner class ValueSet(val values: Array<Double>, val color: Color)
    inner class AxisInfo(val value: String, val pos: Double)

    private var values = mutableListOf<ValueSet>()
    private var xAxisText = mutableListOf<AxisInfo>()
    private var yAxisText = mutableListOf<AxisInfo>()

    fun clear() {
        values.clear()
        xAxisText.clear()
        yAxisText.clear()
    }

    fun addValues(values: Array<Double>, color: Color) {
        this.values.add(ValueSet(values, color))
    }

    fun addXAxisText(value: String, pos: Double) {
        xAxisText.add(AxisInfo(value, pos))
    }

    fun addYAxisText(value: String, pos: Double) {
        yAxisText.add(AxisInfo(value, pos))
    }

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

            val h = canvas.height
            for (i in 0 until row.values.size - 1) {
                canvas.graphicsContext2D.strokeLine(x, h - h * row.values[i], x + step, h - h * row.values[i + 1])
                x += step
            }
        }
    }

    private fun drawAxisInfo() {
        if (!xAxisText.isEmpty()) {
            // draw
        }
        if (!yAxisText.isEmpty()) {
            //draw
        }
    }
}