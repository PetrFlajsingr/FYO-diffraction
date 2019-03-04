package cz.vutbr.fit.xflajs00.fyo

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

@Suppress("NonAsciiCharacters", "PropertyName", "PrivatePropertyName", "LocalVariableName")
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
        val β = (π * a * sin(θ)) / (λ * D)
        val γ = (π * b * sin(θ)) / (λ * D)
        return (sin(β).pow(2) / β.pow(2)) * ((sin(N * γ).pow(2)) / (sin(γ).pow(2)))
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

    override fun toString(): String {
        return "λ = $λ a = $a b = $b D = $D N = $N"
    }
}
