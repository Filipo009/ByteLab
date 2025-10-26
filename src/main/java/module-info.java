module me.filip_jakubowski.bytelab {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.filip_jakubowski.bytelab to javafx.fxml;
    exports me.filip_jakubowski.bytelab;
}