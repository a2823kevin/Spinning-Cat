
module edu.proj {
    requires transitive java.desktop;
    requires javafx.base;
    requires javafx.controls;
    requires transitive javafx.swing;
    requires transitive javafx.graphics;
    requires transitive jcef;
    requires jcefmaven;
    requires transitive java.sql;
    exports edu.proj;
}
