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
            if (row.values.isEmpty()) {
                continue
            }
            // draw
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