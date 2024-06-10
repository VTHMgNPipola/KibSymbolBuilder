package com.vthmgnpipola.kibsymbolbuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.ResourceBundle;

public class KibSymbolBuilder extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);

        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("com.vthmgnpipola.kibsymbolbuilder.KibSymbolBuilder");

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resourceBundle);

        Parent root = loader.load(Objects.requireNonNull(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KSymbolEditorPanel.fxml")).openStream()));

        stage.setTitle(resourceBundle.getString("symbolEditor.title"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
