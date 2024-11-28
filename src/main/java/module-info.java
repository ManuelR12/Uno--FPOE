module com.example.unofpoe {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.unofpoe to javafx.fxml;
    exports com.example.unofpoe;
}