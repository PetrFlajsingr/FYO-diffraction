package cz.vutbr.fit.xflajs00.fyo

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

fun drawIntensity(canvas: Canvas, tmp: Array<Double>, waveLength: Double, scaleFactor: Double = 1.0) {
    val step = tmp.size / canvas.width
    var curPos = 0.0
    val rgb = waveLengthToRGB(waveLength)
    for (i in 0 until canvas.width.toInt()) {
        val intensityColor = getColor(rgb, tmp[curPos.toInt()], scaleFactor)
        canvas.graphicsContext2D.fill = intensityColor
        canvas.graphicsContext2D.stroke = intensityColor
        canvas.graphicsContext2D.strokeLine(i.toDouble(), 0.0, i.toDouble(), canvas.height)
        curPos += step
    }
}

fun getColor(rgb: DoubleArray, intensity: Double, scaleFactor: Double): Color {
    var red = rgb[0] * intensity * scaleFactor
    if (red > 1.0) {
        red = 1.0
    }
    var green = rgb[1] * intensity * scaleFactor
    if (green > 1.0) {
        green = 1.0
    }
    var blue = rgb[2] * intensity * scaleFactor
    if (blue > 1.0) {
        blue = 1.0
    }
    return Color(red, green, blue, 1.0)
}

fun waveLengthToRGB(wavelength: Double): DoubleArray {
    val wlen = wavelength * 1.0e9
    val gamma = 1.0
    val factor: Double
    val red: Double
    val green: Double
    val blue: Double

    if (wlen >= 380 && wlen < 440) {
        red = -(wlen - 440) / (440 - 380)
        green = 0.0
        blue = 1.0
    } else if (wlen >= 440 && wlen < 490) {
        red = 0.0
        green = (wlen - 440) / (490 - 440)
        blue = 1.0
    } else if (wlen >= 490 && wlen < 510) {
        red = 0.0
        green = 1.0
        blue = -(wlen - 510) / (510 - 490)
    } else if (wlen >= 510 && wlen < 580) {
        red = (wlen - 510) / (580 - 510)
        green = 1.0
        blue = 0.0
    } else if (wlen >= 580 && wlen < 645) {
        red = 1.0
        green = -(wlen - 645) / (645 - 580)
        blue = 0.0
    } else if (wlen >= 645 && wlen < 781) {
        red = 1.0
        green = 0.0
        blue = 0.0
    } else {
        red = 1.0
        green = 1.0
        blue = 1.0
    }

    factor = if (wlen >= 380 && wlen < 420) {
        0.3 + 0.7 * (wlen - 380) / (420 - 380)
    } else if (wlen >= 420 && wavelength < 701) {
        1.0
    } else if (wlen >= 701 && wlen < 781) {
        0.3 + 0.7 * (780 - wlen) / (780 - 700)
    } else {
        0.5
    }

    val rgb = DoubleArray(3)
    rgb[0] = if (red == 0.0) 0.01 else Math.pow(red * factor, gamma)
    rgb[1] = if (green == 0.0) 0.01 else Math.pow(green * factor, gamma)
    rgb[2] = if (blue == 0.0) 0.01 else Math.pow(blue * factor, gamma)

    return rgb
}