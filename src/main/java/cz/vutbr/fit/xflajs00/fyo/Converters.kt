package cz.vutbr.fit.xflajs00.fyo

import javafx.util.converter.NumberStringConverter

class NumberIntStringConverter : NumberStringConverter() {

    override fun toString(value: Number?): String {
        return value?.toInt().toString()
    }

    override fun fromString(value: String?): Number {
        return value?.toIntOrNull() ?: return 0
    }
}