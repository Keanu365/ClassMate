package com.example.classmate.Controller;

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
    void generate(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("ClassMate - UML Diagram Editor");
        stage.getIcons().add(new javafx.scene.image.Image("file:" + System.getProperty("user.dir") + "/src/main/resources/com/example/classmate/View/icon.png"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/classmate/View/uml-editor-view.fxml"));
        Parent root = loader.load();
        UMLEditorController controller = loader.getController();

        Scene scene = new Scene(root);
        scene.setUserData(controller);

        stage.setScene(scene);
        stage.show();
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
                    umlClasses[i] = new  UMLClass(classes[i]);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void instructions(MouseEvent event) {
        //TODO #2: Fix UMLClass.loadFolder() code
        //TODO #3: Scene transition animation
        try {
            showScene(new Stage(), "ClassMate - UML Diagram Generator - What to upload?", "/com/example/classmate/View/uml-tut-view.fxml", //Replace path with new FXML file
                    instructionsBtn.getScene().getWidth(), instructionsBtn.getScene().getHeight());
        }catch (Exception e){
            System.out.println("File not found.");
        }
    }

}
