package cz.vutbr.fit.xflajs00.fyo.controllers

import cz.vutbr.fit.xflajs00.fyo.models.ConfigModel
import cz.vutbr.fit.xflajs00.fyo.models.LightSourceModel
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell


class SettingsController {
    @FXML
    private var configTable: TableView<ConfigModel>? = null
    @FXML
    private var combWaveLengthList: ListView<String>? = null
    @FXML
    private var memo: TextArea? = null

    var lightSources = mutableListOf<LightSourceModel>()

    @FXML
    fun initialize() {
        configTable?.isEditable = true
        val valueColumn = TableColumn<ConfigModel, String>("Value")
        valueColumn.cellFactory = TextFieldTableCell.forTableColumn<ConfigModel>()
        valueColumn.setOnEditCommit { event ->
            val evt = event as TableColumn.CellEditEvent<ConfigModel, String>
            val model = evt.tableView.items[evt.tablePosition.row]
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

        for (lightSource in lightSources) {
            combWaveLengthList?.items?.add(lightSource.name)
        }

        combWaveLengthList?.selectionModel?.selectionMode = SelectionMode.SINGLE
        combWaveLengthList?.selectionModel?.selectedIndices?.addListener(ListChangeListener {
            val index = combWaveLengthList!!.selectionModel!!.selectedIndex
            memo?.text = lightSources[index].memoString()
        })

        combWaveLengthList!!.selectionModel!!.select(0)

        memo?.textProperty()?.addListener { _, _, _ -> memo?.styleClass?.add("error"); }
    }

    fun onClose() {
        val alert = Alert(AlertType.INFORMATION)
        alert.title = "Information"
        alert.headerText = "Information"
        alert.contentText = "Rerun the application to apply changes"

        ConfigModel.saveToConfig(configTable!!.items)

        alert.showAndWait()
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
}
