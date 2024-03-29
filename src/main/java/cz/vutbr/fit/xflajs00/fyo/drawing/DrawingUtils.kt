package cz.vutbr.fit.xflajs00.fyo.drawing

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import kotlin.math.ceil


class Intensity(val waveLength: Double, val intensities: Array<Double>)
class Resolution(val width: Int, val height: Int)

/**
 * Draw intensities map on a provided canvas.
 * @param canvas canvas to draw on
 * @param intensity intensities in given points and its color
 * @param scaleFactor intensities scaling factor
 */
fun drawIntensity(canvas: Canvas, intensity: Intensity, scaleFactor: Double = 1.0, drawColor: Boolean = true) {
    canvas.graphicsContext2D.fill = Color.BLACK
    canvas.graphicsContext2D.fillRect(0.0, 0.0, canvas.width, canvas.height)
    val step = intensity.intensities.size / canvas.width
    var curPos = 0.0
    val rgb = waveLengthToRGB(intensity.waveLength, 0.01)
    for (i in 0 until canvas.width.toInt()) {
        val count = ceil(step).toInt()
        var wIntensity: Double
        val curIntPos = curPos.toInt()

        wIntensity = if (curIntPos + count < intensity.intensities.size) {
            intensity.intensities.sliceArray(IntRange(curIntPos, curIntPos + count)).max()!!
        } else {
            intensity.intensities[curIntPos]
        }

        val intensityColor = if (drawColor) {
            getColor(rgb, wIntensity, scaleFactor)
        } else {
            getColor(doubleArrayOf(1.0, 1.0, 1.0), wIntensity, scaleFactor)
        }
        canvas.graphicsContext2D.fill = intensityColor
        canvas.graphicsContext2D.stroke = intensityColor
        canvas.graphicsContext2D.strokeLine(i.toDouble(), 0.0, i.toDouble(), canvas.height)
        curPos += step
    }
}

/**
 * Draw intensities map based on provided intensities.
 * @param canvas canvas to draw on
 * @param intensities
 * #param scaleFactor intensities scaling factor
 */
fun drawCombinedIntensity(canvas: Canvas, intensities: List<Intensity>, scaleFactor: Double = 1.0, drawColor: Boolean = true) {
    canvas.graphicsContext2D.fill = Color.BLACK
    canvas.graphicsContext2D.fillRect(0.0, 0.0, canvas.width, canvas.height)
    val resultColors = Array(canvas.width.toInt()) { DoubleArray(3) { 0.0 } }
    for (intensity in intensities) {
        val step = intensity.intensities.size / canvas.width
        var curPos = 0.0
        val color = waveLengthToRGB(intensity.waveLength)
        for (i in 0 until canvas.width.toInt()) {
            val intensityColor = if (drawColor) {
                getColor(color, intensity.intensities[curPos.toInt()], scaleFactor)
            } else {
                getColor(doubleArrayOf(1.0, 1.0, 1.0), intensity.intensities[curPos.toInt()], scaleFactor)
            }
            resultColors[i][0] += intensityColor.red
            resultColors[i][1] += intensityColor.green
            resultColors[i][2] += intensityColor.blue
            curPos += step
        }
    }
    for (i in 0 until resultColors.size) {
        if (resultColors[i].any { v -> v > 1.0 }) {
            resultColors[i] = normalise(resultColors[i])
        }
        val intensityColor = Color(resultColors[i][0], resultColors[i][1], resultColors[i][2], 1.0)
        canvas.graphicsContext2D.fill = intensityColor
        canvas.graphicsContext2D.stroke = intensityColor
        canvas.graphicsContext2D.strokeLine(i.toDouble(), 0.0, i.toDouble(), canvas.height)
    }
}

fun normalise(values: DoubleArray): DoubleArray {
    val result = values.clone()
    val max = result.max()!!
    for (i in 0 until result.size) {
        result[i] = result[i] / max
    }
    return result
}

/**
 * Convert RGB array to Color. Modify it by intensities and scale factor.
 */
fun getColor(rgb: DoubleArray, intensity: Double, scaleFactor: Double = 1.0): Color {
    var rgbResult = rgb.clone()
    rgbResult[0] = rgb[0] * intensity * scaleFactor
    rgbResult[1] = rgb[1] * intensity * scaleFactor
    rgbResult[2] = rgb[2] * intensity * scaleFactor
    if (rgbResult.any { v -> v > 1.0 }) {
        rgbResult = normalise(rgbResult)
    }
    return Color(rgbResult[0], rgbResult[1], rgbResult[2], 1.0)
}

/**
 * Convert wavelength to RGB approximation.
 * When the value is outside the visible spectrum the computed RGB is Color.WHITE
 *
 * @param wavelength wavelength of light in meters
 * @return array of length 3 containing normalised RGB values
 */
fun waveLengthToRGB(wavelength: Double, defVal: Double = 0.0): DoubleArray {
    val wlen = wavelength * 1.0e9
    val gamma = 1.0
    val factor: Double
    var red = 1.0
    var green = 1.0
    var blue = 1.0

    if (wlen < 440) {
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
    } else if (wlen >= 645) {
        red = 1.0
        green = 0.0
        blue = 0.0
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
    rgb[0] = if (red == 0.0) defVal else Math.pow(red * factor, gamma)
    rgb[1] = if (green == 0.0) defVal else Math.pow(green * factor, gamma)
    rgb[2] = if (blue == 0.0) defVal else Math.pow(blue * factor, gamma)

    if (rgb[0] > 1.0) {
        rgb[0] = 1.0
    }
    if (rgb[1] > 1.0) {
        rgb[1] = 1.0
    }
    if (rgb[2] > 1.0) {
        rgb[2] = 1.0
    }

    return rgb
}