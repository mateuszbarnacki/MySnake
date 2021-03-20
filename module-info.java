module Snake {
    requires javafx.controls;
    requires javafx.fxml;

    opens MySnake.GameElements;
    opens MySnake.Style;
    opens MySnake.Utilities;
    opens MySnake.Window;
    opens MySnake;
}