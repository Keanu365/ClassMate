package com.example.classmate.Controller;

import com.example.classmate.HelloApplication;
import com.example.classmate.Model.UMLClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class UMLController extends Controller{

    @FXML
    private Label backBtn;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button generateBtn;

    @FXML
    private Label selectedFolderLbl;

    @FXML
    private CheckBox simpleNamesCheckbox;

    @FXML
    private Label simpleNamesLbl;

    @FXML
    private Button uploadBtn;

    @FXML
    private Button instructionsBtn;

    static UMLClass[] umlClasses;

    @FXML
    public void initialize(){umlClasses = null;}

    @FXML
    void generate(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("ClassMate - UML Diagram Editor");
        stage.getIcons().add(new javafx.scene.image.Image("file:" + System.getProperty("user.dir") + "/src/main/resources/com/example/classmate/View/icon.png"));
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("View/splash-screen.fxml"));
        SplashScreenController.fxmlToShow = "View/uml-editor-view.fxml";
        SplashScreenController.title = "ClassMate - UML Diagram Editor";
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        ((Stage) generateBtn.getScene().getWindow()).close();
    }

    @FXML
    void upload(MouseEvent event) {
        DirectoryChooser dc  = new DirectoryChooser();
        dc.setTitle("Select Folder");
        File folder = dc.showDialog(uploadBtn.getScene().getWindow());
        if (folder != null){
            try{
                selectedFolderLbl.setText("Selected folder: " + folder.getAbsolutePath());
                File[] files = folder.listFiles();
                if (files == null || files.length == 0) throw new Exception("Provided folder is empty.");
                Class<?>[] classes = UMLClass.loadFolder(folder);
                umlClasses = new UMLClass[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    umlClasses[i] = new UMLClass(classes[i]);
                }
            }catch (Exception e){
                System.err.println("An error occurred.");
            }
        }
    }

    @FXML
    void instructions(MouseEvent event) {
        try {
            showScene(new Stage(), "ClassMate - UML Diagram Generator - What to upload?", "View/uml-tut-view.fxml");
        }catch (Exception e){
            System.out.println("File not found.");
        }
    }

    @Override
    String generateAI(String prompt) {
        return "";
    }
}
