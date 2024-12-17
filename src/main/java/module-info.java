module org.example.eiscuno {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;




    opens org.example.eiscuno to javafx.fxml;
    opens org.example.eiscuno.controller to javafx.fxml;
    opens org.example.eiscuno.model.card to org.junit.jupiter.api;
    opens org.example.eiscuno.model.deck to org.junit.jupiter.api;
    opens org.example.eiscuno.model.table to org.junit.jupiter.api;
    exports org.example.eiscuno;
}