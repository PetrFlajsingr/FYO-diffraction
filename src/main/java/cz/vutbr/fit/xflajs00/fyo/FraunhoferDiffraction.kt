package cz.vutbr.fit.xflajs00.fyo

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

@Suppress("NonAsciiCharacters", "PropertyName", "PrivatePropertyName", "LocalVariableName")
/**
 * Calculation of Fraunhofer Diffraction in far field
 */
class FraunhoferDiffraction {
    val π = 3.14159265359
    /**
     * Wavelength
     */
    var λ = 1.0
        set(value) {
            field = value
            λD = λ * D
        }
    /**
     * Slit width
     */
    var a = 1.0
        set(value) {
            field = value
            πa = π * a
        }
    /**
     * Slit distance
     */
    var b = 1.0
        set(value) {
            field = value
            πb = π * b
        }
    /**
     * Projection plane distance
     */
    var D: Double = 1.0
        set(value) {
            field = value
            λD = λ * D
        }
    /**
     * Slit count
     */
    var N: Int = 1
    // vars for computation speed increase
    private var πa = 0.0
    private var πb = 0.0
    private var λD = 0.0

    /**
     * Calculate intensity for angle θ
     * @param θ angle on projection plane
     * @return wave intensity in given position
     */
    private fun calcFor(θ: Double): Double {
        val sinθ = sin(θ)
        val β = (πa * sinθ) / (λD)
        val γ = (πb * sinθ) / (λD)
        return (sin(β).pow(2) / β.pow(2)) * ((sin(N * γ).pow(2)) / (sin(γ).pow(2)))
    }

    /**
     * Calculate intensity over given area.
     * Computes only the first half of the area and mirrors it on the other side - function is symmetrical.
     * Normalises values by maximum intensity
     * @param start left-most value
     * @param end right-most value
     * @param resolution step of calculation
     * @return array of calculated intensities
     */
    fun calcInterval(start: Double, end: Double, resolution: Double): Array<Double> {
        val result: Array<Double> = Array((abs(start - end) / resolution).toInt()) {0.0}
        var x = start
        //  calculate first half
        for (i in 0 until result.size / 2) {
            result[i] = calcFor(x)
            x += resolution
        }
        //  mirror the first half
        val reversed = result.copyOfRange(0, result.size / 2)
        reversed.reverse()
        for (i in result.size / 2 until result.size ) {
            result[i] = reversed[i - result.size / 2]
        }
        //  normalise using maximum intensity
        val max = result.max()
        for (i in 0 until result.size) {
            result[i] /= max!!
        }
        return result
    }

    override fun toString(): String {
        return "λ = $λ a = $a b = $b D = $D N = $N"
    }
}
