module co.edu.uniquindio.clinicaodontologica {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens co.edu.uniquindio.clinicaodontologica to javafx.graphics;
    opens co.edu.uniquindio.clinicaodontologica.view.fx to javafx.graphics, javafx.fxml;
    opens co.edu.uniquindio.clinicaodontologica.model.entity to javafx.base;
    opens co.edu.uniquindio.clinicaodontologica.model.enums to javafx.base;

    exports co.edu.uniquindio.clinicaodontologica;
    exports co.edu.uniquindio.clinicaodontologica.model.entity;
    exports co.edu.uniquindio.clinicaodontologica.model.enums;
    exports co.edu.uniquindio.clinicaodontologica.service;
    exports co.edu.uniquindio.clinicaodontologica.repository;
    exports co.edu.uniquindio.clinicaodontologica.config;
    exports co.edu.uniquindio.clinicaodontologica.presenter;
    exports co.edu.uniquindio.clinicaodontologica.view.contract;
    exports co.edu.uniquindio.clinicaodontologica.view.fx;
}
