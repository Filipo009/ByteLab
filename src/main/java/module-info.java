module me.filip_jakubowski.bytelab {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens me.filip_jakubowski.bytelab to javafx.fxml;
    exports me.filip_jakubowski.bytelab;
    exports me.filip_jakubowski.bytelab.education;
    opens me.filip_jakubowski.bytelab.education to javafx.fxml;
}