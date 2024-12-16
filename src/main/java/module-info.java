module com.example.tetris {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.tetris to javafx.fxml;
    exports com.example.tetris;
}