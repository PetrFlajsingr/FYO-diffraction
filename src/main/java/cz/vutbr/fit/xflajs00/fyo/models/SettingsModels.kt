package cz.vutbr.fit.xflajs00.fyo.models

import javafx.beans.property.SimpleStringProperty
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class LightSourceModel(var name: String, wavelengths: List<String> = emptyList()) {
    var waveLengthIntervals = emptyList<Pair<Double, Double>>()
    var waveLengths = emptyList<Double>()

    init {
        parseWavelengths(wavelengths)
    }

    private fun parseWavelengths(wavelengths: List<String>) {
        val intervals = mutableListOf<Pair<Double, Double>>()
        val values = mutableListOf<Double>()
        for (value in wavelengths) {
            if (value.contains("-")) {
                val split = value.split("-")
                intervals.add(Pair(split[0].toDouble(), split[1].toDouble()))
            } else {
                values.add(value.toDouble())
            }
        }
        waveLengthIntervals = intervals
        waveLengths = values
    }

    fun memoString(): String {
        var result = ""
        for (value in waveLengths) {
            result += "$value\n"
        }
        for (value in waveLengthIntervals) {
            result += "${value.first}-${value.second}\n"
        }
        return result
    }

    fun setFromMemoString(str: String) {
        var innerStr = str
        if (innerStr.endsWith("\n")) {
            innerStr = innerStr.substring(0, str.length - 2)
        }
        parseWavelengths(innerStr.split("\n"))
    }

    override fun toString(): String {
        var result = "$name:"
        for (value in waveLengths) {
            result += "$value,"
        }
        for (value in waveLengthIntervals) {
            result += "${value.first}-${value.second},"
        }
        return if (result.last() == ',') {
            result.substring(0, result.length - 2)
        } else {
            result
        }
    }

    companion object {
        fun fromString(str: String): LightSourceModel {
            val nameEnd = str.indexOf(":")
            return LightSourceModel(str.substring(0, nameEnd), str.substring(nameEnd + 1).split(",").toList())
        }

        fun loadFromConfig(): List<LightSourceModel> {
            val prop = Properties()
            val resourceUrl = javaClass.classLoader.getResource("light_sources.conf")
            val file = FileInputStream(File(resourceUrl.toURI()))
            prop.load(file)
            val result = mutableListOf<LightSourceModel>()
            for (property in prop) {
                result.add(fromString(property.key.toString() + ":" + property.value.toString()))
            }
            return result
        }

        fun saveToConfig(vals: List<LightSourceModel>) {
            val prop = Properties()
            val resourceUrl = javaClass.classLoader.getResource("light_sources.conf")
            val file = File(resourceUrl.toURI())
            for (value in vals) {
                val strRep = value.toString().split(":")
                prop.setProperty(strRep[0], strRep[1])
            }
            prop.save(FileOutputStream(file), "")
        }
    }
}

class ConfigModel(key: String, value: String) {
    private var keyProperty = SimpleStringProperty(key)
    private var valueProperty = SimpleStringProperty(value)

    fun getKey(): String {
        return propKeyToString(keyProperty.get())
    }

    fun setKey(key: String) {
        keyProperty = SimpleStringProperty(key)
    }

    fun getValue(): String {
        return valueProperty.get()
    }

    fun setValue(value: String) {
        valueProperty = SimpleStringProperty(value)
    }

    companion object {
        fun loadFromConfig(): List<ConfigModel> {
            val prop = Properties()
            val resourceUrl = javaClass.classLoader.getResource("settings.conf")
            val file = FileInputStream(File(resourceUrl.toURI()))
            prop.load(file)
            val result = mutableListOf<ConfigModel>()
            for (property in prop) {
                result.add(ConfigModel(property.key.toString(), property.value.toString()))
            }
            return result
        }

        fun saveToConfig(vals: List<ConfigModel>) {
            val prop = Properties()
            val resourceUrl = javaClass.classLoader.getResource("settings.conf")
            val file = File(resourceUrl.toURI())
            for (property in vals) {
                prop.setProperty(propStringToKey(property.getKey()), property.getValue());
            }
            prop.save(FileOutputStream(file), "")
        }

        fun propKeyToString(key: String): String {
            return when (key) {
                "mono_step" -> "Monochromatic calc step"
                "comb_step" -> "Combined calc step"
                "comb_wave_step" -> "Combined wavelength step"
                else -> key
            }
        }

        fun propStringToKey(str: String): String {
            return when (str) {
                "Monochromatic calc step" -> "mono_step"
                "Combined calc step" -> "comb_step"
                "Combined wavelength step" -> "comb_wave_step"
                else -> str
            }
        }
    }
}