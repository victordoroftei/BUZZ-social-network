module com.example.laborator5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    requires com.zaxxer.hikari;

    requires com.github.librepdf.openpdf;
    requires java.desktop;

    opens com.example.laborator5 to javafx.fxml;
    exports com.example.laborator5;
    exports com.example.laborator5.socialnetwork.service;
    exports com.example.laborator5.socialnetwork.utils.observer;
    exports com.example.laborator5.socialnetwork.repository.database;

    opens com.example.laborator5.socialnetwork.service.dto to javafx.base;
    exports com.example.laborator5.socialnetwork.service.dto;

    exports com.example.laborator5.socialnetwork.domain;
}