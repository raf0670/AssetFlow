module com.example.assetflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.assetflow to javafx.fxml;
    exports com.example.assetflow;
}