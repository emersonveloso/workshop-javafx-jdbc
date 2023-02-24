package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listerners.DataChangeListerner;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

    private Department entity;

    private DepartmentService departmentService;

    private List<DataChangeListerner> dataChangeListerners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    public void setDepartment(Department entity) {
        this.entity = entity;
    }

    public void setDepartmentService(DepartmentService service) {
        this.departmentService = service;
    }

    public void subscribeDataChangeListener(DataChangeListerner listerner) {
        dataChangeListerners.add(listerner);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }

        if (departmentService == null) {
            throw new IllegalStateException("Service was null");
        }

        try {
            entity = getFormData();
            departmentService.saveOrUpdate(entity);
            notifyDataChangeListerners();
            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private void notifyDataChangeListerners() {
        for (DataChangeListerner listerner : dataChangeListerners) {
            listerner.onDataChanged();
        }
    }

    private Department getFormData() {
        Department obj = new Department();
        obj.setId(Utils.tryParseToInt(txtId.getText()));
        obj.setName(txtName.getText());
        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }

}
