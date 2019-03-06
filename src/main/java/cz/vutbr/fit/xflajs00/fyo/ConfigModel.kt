package cz.vutbr.fit.xflajs00.fyo

import javafx.beans.property.SimpleStringProperty
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


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
        fun fromConfig(): List<ConfigModel> {
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
                else -> key
            }
        }

        fun propStringToKey(str: String): String {
            return when (str) {
                "Monochromatic calc step" -> "mono_step"
                "Combined calc step" -> "comb_step"
                else -> str
            }
        }
    }
}