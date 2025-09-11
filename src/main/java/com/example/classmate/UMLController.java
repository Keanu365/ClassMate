package com.example.classmate;

import javafx.fxml.FXML;
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
        showScene(new Stage(), "ClassMate - UML Diagram Editor", "/com/example/classmate/View/uml-editor-view.fxml");
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
                //Placeholder code for future work
                for (UMLClass uc : umlClasses){
                    System.out.println(uc);
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
