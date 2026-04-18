package com.paintdecoration.controller;

import com.paintdecoration.model.Service;
import com.paintdecoration.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML private ListView<Service> serviceListView;
    @FXML private ListView<String> cartListView;
    @FXML private Label serviceNameLabel;
    @FXML private Label serviceCategoryLabel;
    @FXML private Label serviceDescriptionLabel;
    @FXML private Label servicePriceLabel;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    private List<Service> selectedServices = new ArrayList<>();

    @FXML
    public void initialize() {
        loadServices();
        serviceListView.setOnMouseClicked(event -> showServiceDetails());
    }

    private void loadServices() {
        List<Service> services = DatabaseService.getAllServices();
        ObservableList<Service> observableServices = FXCollections.observableArrayList(services);
        serviceListView.setItems(observableServices);
    }

    private void showServiceDetails() {
        Service selected = serviceListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            serviceNameLabel.setText(selected.getName());
            serviceCategoryLabel.setText("النوع: " + selected.getCategory());
            serviceDescriptionLabel.setText("الوصف: " + selected.getDescription());
            servicePriceLabel.setText("السعر: " + selected.getPrice() + " دينار");
        }
    }

    @FXML
    private void addToCart() {
        Service selected = serviceListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedServices.add(selected);
            updateCart();
            statusLabel.setText("تمت إضافة الخدمة: " + selected.getName());
        } else {
            showAlert("تنبيه", "يرجى اختيار خدمة أولاً");
        }
    }

    @FXML
    private void removeFromCart() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            selectedServices.remove(selectedIndex);
            updateCart();
            statusLabel.setText("تمت إزالة الخدمة من السلة");
        } else {
            showAlert("تنبيه", "يرجى اختيار خدمة من السلة لحذفها");
        }
    }

    @FXML
    private void confirmOrder() {
        if (selectedServices.isEmpty()) {
            showAlert("تنبيه", "السلة فارغة! يرجى اختيار خدمات");
            return;
        }

        for (Service service : selectedServices) {
            DatabaseService.addOrder(service.getId());
        }

        showAlert("نجاح", "تم تأكيد الطلب بنجاح!\nعدد الخدمات: " + selectedServices.size() + "\nالإجمالي: " + calculateTotal() + " دينار");
        selectedServices.clear();
        updateCart();
        statusLabel.setText("تم تأكيد الطلب بنجاح");
    }

    private void updateCart() {
        ObservableList<String> cartItems = FXCollections.observableArrayList();
        for (Service service : selectedServices) {
            cartItems.add(service.getName() + " - " + service.getPrice() + " دينار");
        }
        cartListView.setItems(cartItems);
        totalLabel.setText(calculateTotal() + " دينار");
    }

    private double calculateTotal() {
        return selectedServices.stream().mapToDouble(Service::getPrice).sum();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}