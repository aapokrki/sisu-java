/**
 * Sisu requirements
 */
module fi.tuni.prog3.sisu {
    requires javafx.controls;
    requires javafx.graphics;
    requires com.google.gson;
    requires javafx.fxml;
    requires org.apache.commons.io;

    opens fi.tuni.prog3.sisu;
    exports fi.tuni.prog3.sisu;


}