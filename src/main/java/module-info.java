module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens chatjfx to javafx.fxml;
    exports chatjfx;
}