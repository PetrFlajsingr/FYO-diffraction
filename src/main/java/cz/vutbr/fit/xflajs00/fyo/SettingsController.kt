package cz.vutbr.fit.xflajs00.fyo

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell


class SettingsController {
    @FXML
    private var configTable: TableView<ConfigModel>? = null

    // TODO: create config
    // TODO: first config init
    // TODO: save config
    // TODO: list of wavelengths
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
        valueColumn.prefWidth = configTable!!.prefWidth / 2
        valueColumn.isEditable = true

        val keyColumn = TableColumn<ConfigModel, String>("Key")
        keyColumn.cellValueFactory = PropertyValueFactory<ConfigModel, String>("key")
        keyColumn.prefWidth = configTable!!.prefWidth / 2

        configTable?.columns?.addAll(keyColumn, valueColumn)

        for (prop in ConfigModel.fromConfig()) {
            configTable?.items?.add(prop)
        }
    }

    fun onClose() {
        val alert = Alert(AlertType.INFORMATION)
        alert.title = "Information"
        alert.headerText = "Information"
        alert.contentText = "Rerun the application to apply changes"

        ConfigModel.saveToConfig(configTable!!.items)

        alert.showAndWait()
    }
}
