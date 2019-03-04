package cz.vutbr.fit.xflajs00.fyo

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

@Suppress("NonAsciiCharacters")
class FraunhoferDiffraction {
    val π: Double = 3.14159265359

    var λ: Double = 1.0
        set(value) {
            field = value
            πbByλ = π * b / λ
            πaByλ = π * a / λ
        }
    var a: Double = 1.0
        set(value) {
            field = value
            πaByλ = π * a / λ
        }
    var b: Double = 1.0
        set(value) {
            field = value
            πbByλ = π * b / λ
        }
    var D: Double = 1.0
    var N: Int = 1

    private var πbByλ: Double = 0.0
    private var πaByλ: Double = 0.0

    private fun calcFor(θ: Double): Double {
        val β = (π * b / λ) * sin(θ)
        val α = π * a / λ * sin(θ)

        val interf = (sin(N * β / 2) / sin(β / 2)).pow(2)
        val diff = (sin(α) / α).pow(2)
        return interf * diff
        /*val xByD = x / D
        val πbByλ_xByD = πbByλ * xByD
        val πaByλ_xByD = πaByλ * xByD
        return 4 * (sin(πbByλ_xByD) / πbByλ_xByD).pow(2) * ((sin(N * πaByλ_xByD) / (N * sin(πaByλ_xByD)))).pow(2)*/
    }

    fun calcInterval(start: Double, end: Double, resolution: Double): Array<Double> {
        val result: Array<Double> = Array((abs(start - end) / resolution).toInt()) {0.0}
        var x = start
        var max = 0.0
        for (i in 0 until result.size / 2) {
            result[i] = calcFor(x)
            x += resolution
            if (result[i] > max) {
                max = result[i]
            }
        }
        val reversed = result.copyOfRange(0, result.size / 2)
        reversed.reverse()
        for (i in result.size / 2 until result.size ) {
            result[i] = reversed[i - result.size / 2]
        }

        for (i in 0 until result.size) {
            result[i] /= max
        }
        return result
    }
}
