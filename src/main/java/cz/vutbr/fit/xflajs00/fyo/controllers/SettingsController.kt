package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.models.ConfigModel
import cz.vutbr.fit.xflajs00.fyo.models.LightSourceModel
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.StringConverter


class SettingsController {
    @FXML
    private var configTable: TableView<ConfigModel>? = null
    @FXML
    private var combWaveLengthList: ListView<String>? = null
    @FXML
    private var memo: TextArea? = null

    var lightSources = mutableListOf<LightSourceModel>()

    private var lastSelectedIndex = -1

    @FXML
    fun initialize() {
        configTable?.isEditable = true
        val valueColumn = TableColumn<ConfigModel, String>("Value")
        valueColumn.cellFactory = TextFieldTableCell.forTableColumn<ConfigModel>()
        valueColumn.setOnEditCommit { event ->
            val evt = event as TableColumn.CellEditEvent<ConfigModel, String>
            if (evt.newValue.toDoubleOrNull() == null) {
                return@setOnEditCommit
            }
            val model = evt.tableView.items[evt.tablePosition.row]
            if (model.getKey() == "comb_step" || model.getKey() == "mono_step") {
                val value = evt.newValue.toIntOrNull()
                if (value == null || value < 100) {
                    return@setOnEditCommit
                }
            }
            model.setValue(evt.newValue)
        }
        valueColumn.cellValueFactory = PropertyValueFactory<ConfigModel, String>("value")
        valueColumn.prefWidth = configTable!!.prefWidth / 2 - 1
        valueColumn.isEditable = true

        val keyColumn = TableColumn<ConfigModel, String>("Key")
        keyColumn.cellValueFactory = PropertyValueFactory<ConfigModel, String>("key")
        keyColumn.prefWidth = configTable!!.prefWidth / 2 - 1

        configTable?.columns?.addAll(keyColumn, valueColumn)

        for (prop in ConfigModel.loadFromConfig()) {
            configTable?.items?.add(prop)
        }

        lightSources = LightSourceModel.loadFromConfig().toMutableList()

        combWaveLengthList?.isEditable = true
        combWaveLengthList?.setCellFactory { lv ->
            val cell = TextFieldListCell<String>()
            cell.converter = object : StringConverter<String>() {
                override fun toString(str: String): String {
                    str.replace(" ", "_")
                    return str
                }

                override fun fromString(str: String): String {
                    return str
                }
            }
            cell
        }
        combWaveLengthList?.setOnEditCommit { event ->
            val evt = event as ListView.EditEvent<String>
            val index = combWaveLengthList!!.selectionModel.selectedIndex
            combWaveLengthList!!.items[index] = evt.newValue
            lightSources[index].name = combWaveLengthList!!.selectionModel.selectedItem

        }
        for (lightSource in lightSources) {
            combWaveLengthList?.items?.add(lightSource.name)
        }

        combWaveLengthList?.selectionModel?.selectionMode = SelectionMode.SINGLE
        combWaveLengthList?.selectionModel?.selectedIndices?.addListener(ListChangeListener {
            if (lastSelectedIndex != -1 && !memo!!.text.isEmpty()) {
                waveLengthsToSource(lastSelectedIndex, memo!!.text)
            }
            val index = combWaveLengthList!!.selectionModel!!.selectedIndex
            memo?.text = lightSources[index].memoString()
            lastSelectedIndex = index
        })

        combWaveLengthList!!.selectionModel!!.select(0)
    }

    fun onClose() {
        ConfigModel.saveToConfig(configTable!!.items)
        LightSourceModel.saveToConfig(lightSources)
    }

    fun addLightSource() {
        lightSources.add(LightSourceModel("New light source"))
        combWaveLengthList?.items?.add(lightSources.last().name)
    }

    fun removeLightSource() {
        val index = combWaveLengthList!!.selectionModel!!.selectedIndex
        lightSources.removeAt(index)
        combWaveLengthList!!.items!!.removeAt(index)
    }

    private fun waveLengthsToSource(index: Int, str: String) {
        lightSources[index].setFromMemoString(str)
    }
}
