package com.example.classmate.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        chatAreaPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //System.out.println("Scene Width changed from " + oldValue + " to " + newValue);
                chatArea.setPrefWidth(chatAreaPane.getWidth()-15);
            }
        });
    }

    @FXML
    void submit(InputEvent event) throws InterruptedException {
        submitBtn.setDisable(userTextField.getText().isEmpty());
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
        task.setOnSucceeded(e -> {
            response.setText(task.getValue());
        });
        task.setOnFailed(e -> {
            response.setText("Something went wrong. Please try again later.");
        });

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
