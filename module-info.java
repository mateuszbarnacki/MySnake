module Snake {
    requires javafx.controls;
    requires javafx.fxml;

    opens sample.GameElements;
    opens sample.Style;
    opens sample.Utilities;
    opens sample.Window;
    opens sample;
}