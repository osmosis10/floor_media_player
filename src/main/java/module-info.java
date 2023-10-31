module com.example.mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens com.example.mediaplayer to javafx.fxml;
    exports com.example.mediaplayer;
}