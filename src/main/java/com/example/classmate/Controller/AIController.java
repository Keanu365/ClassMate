package com.example.classmate.Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class AIController extends Controller{

    @FXML
    private VBox chatArea;

    @FXML
    private Button submitBtn;

    @FXML
    private TextField userTextField;

    @FXML
    private ScrollPane chatAreaPane;

    @FXML
    void initialize(){
        chatAreaPane.widthProperty().addListener((_, _, _) -> {
            //System.out.println("Scene Width changed from " + oldValue + " to " + newValue);
            chatArea.setPrefWidth(chatAreaPane.getWidth()-15);
        });
        submitBtn.disableProperty().bind(userTextField.textProperty().isEmpty());
    }

    @FXML
    void submit(InputEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.getCode() != KeyCode.ENTER) return;
        }
        String input = userTextField.getText();
        newTextLabel(input, "User");
        Label response = newTextLabel("Generating response...", "AI");
        userTextField.clear();

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return generateAI(input);
            }
        };
        task.setOnSucceeded(_ -> response.setText(task.getValue()));
        task.setOnFailed(_ -> response.setText("Something went wrong. Please try again later."));

        new Thread(task).start();
    }

    Label newTextLabel(String content, String type){ //type: AI/User
        chatArea.setPrefWidth(chatAreaPane.getWidth()-15);
        Label label = new Label(content);
        label.getStyleClass().add("response");
        HBox row = null;
        if (type.equals("User")){
            label.getStyleClass().add("user_response");
//            VBox.setMargin(label, VBox.getMargin(userTextSample));
            row = new HBox(label);
            HBox.setHgrow(row,  Priority.ALWAYS);
            row.setAlignment(Pos.CENTER_RIGHT);
        }
        else if (type.equals("AI")){
            label.getStyleClass().add("ai_response");
//            VBox.setMargin(label, VBox.getMargin(aiTextSample));
            row = new HBox(label);
            HBox.setHgrow(row,  Priority.ALWAYS);
            row.setAlignment(Pos.CENTER_LEFT);
        }
        chatArea.getChildren().add(row);
        return label;
    }

}
