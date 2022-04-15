package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Implements a graphical user interface for checking studies in Sisu
 */
public class Sisu extends Application {

    /**
     * Main function
     * @param args - program arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the program
     * @param stage - stage
     */
    public void start(Stage stage) {

        stage.setTitle("SISU");

        Scene startScene = getStartScreen(stage);
        stage.setScene(startScene);
        stage.show();

        // Needs some kind of a check to be able to start the mainView
        mainView(new Student());
    }

    /**
     * Handles text fields for correct inputs
     * @param textField - text field
     * @param onlyIntegers - boolean value representing whether textField takes integers (years) or not
     */
    private void addInputListener(TextField textField, boolean onlyIntegers) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (onlyIntegers && !newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
                newValue = "";
            }

            if (!onlyIntegers && newValue.isBlank()) {
                textField.setStyle("-fx-text-box-border: 'red';");
            } else {
                textField.setStyle("-fx-text-box-border: 'green';");
            }
        });
    }

    /**
     * Returns the start screen
     * @param stage - stage
     * @return start screen
     */
    private Scene getStartScreen(Stage stage) {

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox,350,280);

        vBox.setSpacing(20);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Welcome to Sisu!");

        HBox buttons = new HBox();

        ToggleButton loginButton = new ToggleButton("Login");
        loginButton.setOnAction( e -> stage.setScene(getLoginScreen(stage)) );

        ToggleButton registerButton = new ToggleButton("Register");
        registerButton.setOnAction( e -> stage.setScene(getRegistrationScreen(stage)));

        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(loginButton, registerButton);

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(title, buttons);

        return scene;
    }

    /**
     * Returns the login screen
     * @param stage - stage
     * @return login screen
     */
    private Scene getLoginScreen(Stage stage) {
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 350, 280);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);


        Label title = new Label("Login by entering student number");
        grid.add(title,0,0,2,1);

        Label studentNumberLabel = new Label("Student number:");
        grid.add(studentNumberLabel,0,2);
        TextField studentNumberTextField = new TextField();
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField,1,2);

        Label errorMessage = new Label();
        errorMessage.setTextFill(Color.RED);
        errorMessage.setStyle("-fx-font-size: 10px;");
        grid.add(errorMessage,1,3);

        HBox buttons = new HBox();

        ToggleButton backButton = new ToggleButton("Back");
        backButton.setOnAction( e -> stage.setScene(getStartScreen(stage)) );

        ToggleButton loginButton = new ToggleButton("login");
        loginButton.setOnAction( e -> {
            //Check the database if student number exists

            if (studentNumberTextField.getText().trim().isEmpty()) {
                studentNumberTextField.setStyle("-fx-text-box-border: 'red';");
                errorMessage.setText("Invalid student number");
            }
        });

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(backButton, loginButton);

        grid.add(buttons, 1, 4);

        return scene;
    }

    /**
     * Returns the registration screen
     * @param stage - stage
     * @return registration screen
     */
    private Scene getRegistrationScreen(Stage stage) {

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid,350,280);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        Label title = new Label("Register a profile");
        grid.add(title, 0, 0, 2, 1);

        Label nameLabel = new Label("Name:");
        grid.add(nameLabel, 0, 2);
        TextField nameTextField = new TextField();
        addInputListener(nameTextField, false);
        grid.add(nameTextField, 1, 2);

        Label studentNumberLabel = new Label("Student number:");
        grid.add(studentNumberLabel, 0, 4);
        TextField studentNumberTextField = new TextField();
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField, 1, 4);

        Label startYearMessage = new Label("optional");
        startYearMessage.setStyle("-fx-font-size: 10px;");
        GridPane.setHalignment(startYearMessage, HPos.RIGHT);
        grid.add(startYearMessage,1,6);

        Label startYearLabel = new Label("Start year:");
        grid.add(startYearLabel, 0, 7);
        TextField startYearTextField = new TextField();
        addInputListener(startYearTextField, true);
        grid.add(startYearTextField, 1, 7);

        Label endYearMessage = new Label("optional");
        GridPane.setHalignment(endYearMessage, HPos.RIGHT);
        endYearMessage.setStyle("-fx-font-size: 10px;");
        grid.add(endYearMessage,1,8);

        Label endYearLabel = new Label("Estimated end year:");
        grid.add(endYearLabel, 0, 9);
        TextField endYearTextField = new TextField();
        addInputListener(endYearTextField, true);
        grid.add(endYearTextField, 1, 9);

        HBox buttons = new HBox();

        ToggleButton createButton = new ToggleButton("Register");
        createButton.setOnAction(e -> {

            boolean notEmpty = true;

            if (nameTextField.getText().trim().isEmpty()) {
                nameTextField.setStyle("-fx-text-box-border: 'red';");

                Label nameErrorMessage = new Label("Invalid name");
                nameErrorMessage.setTextFill(Color.RED);
                nameErrorMessage.setStyle("-fx-font-size: 10px;");
                grid.add(nameErrorMessage,1,3);
                notEmpty = false;
            } else {
                nameTextField.setStyle("-fx-text-box-border: 'green';");
            }

            if (studentNumberTextField.getText().trim().isEmpty()) {
                studentNumberTextField.setStyle("-fx-text-box-border: 'red';");
                Label studentNumberErrorMessage = new Label("Invalid student number");
                studentNumberErrorMessage.setTextFill(Color.RED);
                studentNumberErrorMessage.setStyle("-fx-font-size: 10px;");
                grid.add(studentNumberErrorMessage,1,5);

                notEmpty = false;
            } else {
                studentNumberTextField.setStyle("-fx-text-box-border: 'green';");
            }

            if (notEmpty) {

                // Create a new account

                stage.close();
            }
        });

        ToggleButton backButton = new ToggleButton("Back");
        backButton.setOnAction( e -> stage.setScene(getStartScreen(stage)) );

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(backButton, createButton);

        grid.add(buttons, 1,10);

        return scene;
    }

    /**
     * Returns the side menu
     * @param stage - stage
     * @param student - Student class member
     * @return side menu
     */
    private VBox getSideMenu(Stage stage, Student student) {
        VBox menu = new VBox();

        menu.setSpacing(5);

        ToggleButton structureOfStudiesButton = new ToggleButton("Structure \nof studies");
        structureOfStudiesButton.setTextAlignment(TextAlignment.CENTER);
        structureOfStudiesButton.setPrefSize(80,40);
        structureOfStudiesButton.setOnAction( e -> stage.setScene(getStructureOfStudiesScreen(stage, student)) );

        ToggleButton studentInformationButton = new ToggleButton("Student \ninformation");
        studentInformationButton.setTextAlignment(TextAlignment.CENTER);
        studentInformationButton.setPrefSize(80,40);
        studentInformationButton.setOnAction(e -> stage.setScene(getStudentInformationScreen(stage, student)) );

        menu.getChildren().addAll(studentInformationButton, structureOfStudiesButton);

        return menu;
    }

    /**
     * Implements the main view of the program
     * @param student - Student class member
     */
    private void mainView(Student student) {

        Stage stage = new Stage();
        stage.setTitle("SISU");

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10,10,10,10));

        borderPane.setLeft(getSideMenu(stage, student));

        Scene scene = new Scene(borderPane, 600, 400);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns student information screen
     * @param stage - stage
     * @param student - Student class member
     * @return student information screen
     */
    private Scene getStudentInformationScreen(Stage stage, Student student) {

        return new Scene(new HBox());
    }

    /**
     * Returns structure of studies screen
     * @param stage - stage
     * @param student - Student class member
     * @return structure of studies screen
     */
    private Scene getStructureOfStudiesScreen(Stage stage, Student student) {

        return new Scene(new HBox());
    }
}