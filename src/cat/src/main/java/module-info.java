
module edu.proj {
    requires transitive java.desktop;
    requires transitive java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.io;
    requires org.apache.commons.compress;
    requires javafx.base;
    requires javafx.controls;
    requires transitive javafx.swing;
    requires transitive javafx.graphics;
    requires transitive jcef;
    requires jcefmaven;
    requires obj;

    exports edu.proj;
}
