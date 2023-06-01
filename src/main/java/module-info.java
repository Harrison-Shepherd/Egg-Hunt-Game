module com.example.buttontest {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.EggHuntGame to javafx.fxml;
    exports com.example.EggHuntGame;
}