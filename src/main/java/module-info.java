module com.musicalcalculator.musicalcalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.musicalcalculator.musicalcalculator to javafx.fxml;
    exports com.musicalcalculator.musicalcalculator;
}