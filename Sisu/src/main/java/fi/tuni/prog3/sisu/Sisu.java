package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.css.Style;
import javafx.css.StyleClass;
import javafx.css.Stylesheet;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;

/**
 * Implements a graphical user interface for checking studies in Sisu
 */
public class Sisu extends Application {

    private Stage stage;
    private final String style = this.getClass().getResource("/Sisu.css").toExternalForm();

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

        this.stage = stage;
        this.stage.setTitle("SISU");
        Scene startScene = getStartScreen();

        stage.setScene(startScene);
        stage.show();

        // Needs some kind of a check to be able to start the mainView
        //mainView(new Student());
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
                textField.getStyleClass().add("textFieldFalse");
            } else {
                textField.getStyleClass().add("textFieldTrue");
            }
        });
    }

    /**
     * Returns the start screen
     * @return start screen
     */
    private Scene getStartScreen() {

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox,400,300);
        //scene.setFill(Paint.valueOf("#f2f2f2"));
        vBox.setBackground(Background.EMPTY);
        scene.setFill(Paint.valueOf("#f2f2f2"));

        scene.getStylesheets().add(this.style);

        vBox.setSpacing(20);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Welcome to Sisu!");
        title.getStyleClass().add("welcomeLabel");

        HBox buttons = new HBox();

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.getStyleClass().add("whiteButton");
        loginButton.setOnAction( e -> stage.setScene(getLoginScreen()) );

        ToggleButton registerButton = new ToggleButton("REGISTER");
        registerButton.getStyleClass().add("whiteButton");
        registerButton.setOnAction( e -> stage.setScene(getRegistrationScreen()));

        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(15);
        buttons.getChildren().addAll(loginButton, registerButton);

        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(title, buttons);
        buttons.getParent().requestFocus();

        return scene;
    }

    /**
     * Returns the login screen
     * @return login screen
     */
    private Scene getLoginScreen() {
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 400, 300);
        scene.getStylesheets().add(this.style);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);


        Label title = new Label("Login by entering student number");
        title.getStyleClass().add("titleLabel");

        grid.add(title,0,0,2,1);

        Label studentNumberLabel = new Label("Student number:");
        studentNumberLabel.getStyleClass().add("textLabel");
        grid.add(studentNumberLabel,0,2);

        TextField studentNumberTextField = new TextField();
        studentNumberTextField.getStyleClass().add("textField");
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField,1,2);

        Label errorMessage = new Label();
        errorMessage.getStyleClass().add("errorLabel");
        grid.add(errorMessage,1,3);

        HBox buttons = new HBox();

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.getStyleClass().add("whiteButton");
        backButton.setOnAction( e -> stage.setScene(getStartScreen()) );

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.getStyleClass().add("blueButton");
        loginButton.setOnAction( e -> {
            //Check the database if student number exists

            if (studentNumberTextField.getText().trim().isEmpty()) {
                studentNumberTextField.getStyleClass().add("textFieldFalse");
                errorMessage.setText("Invalid student number");
            }
        });

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(15);
        buttons.getChildren().addAll(backButton, loginButton);

        grid.add(buttons, 1, 4);
        buttons.getParent().requestFocus();

        return scene;
    }

    /**
     * Returns the registration screen
     * @return registration screen
     */
    private Scene getRegistrationScreen() {

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid,400,300);
        scene.getStylesheets().add(this.style);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        Label title = new Label("Register a profile");
        title.getStyleClass().add("titleLabel");
        grid.add(title, 0, 0, 2, 1);

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("textLabel");
        grid.add(nameLabel, 0, 2);

        TextField nameTextField = new TextField();
        nameTextField.getStyleClass().add("textField");
        addInputListener(nameTextField, false);
        grid.add(nameTextField, 1, 2);

        Label studentNumberLabel = new Label("Student number:");
        studentNumberLabel.getStyleClass().add("textLabel");
        grid.add(studentNumberLabel, 0, 4);

        TextField studentNumberTextField = new TextField();
        studentNumberTextField.getStyleClass().add("textField");
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField, 1, 4);

        Label startYearLabel = new Label("Start year (optional):");
        startYearLabel.getStyleClass().add("textLabel");
        GridPane.setMargin(startYearLabel, new Insets(0, 0, 12, 0));
        grid.add(startYearLabel, 0, 6);


        TextField startYearTextField = new TextField();
        startYearTextField.getStyleClass().add("textField");
        GridPane.setMargin(startYearTextField, new Insets(0, 0, 12, 0));
        addInputListener(startYearTextField, true);
        grid.add(startYearTextField, 1, 6);

        Label endYearLabel = new Label("Estimated end year (optional):");
        endYearLabel.getStyleClass().add("textLabel");
        grid.add(endYearLabel, 0, 8);

        TextField endYearTextField = new TextField();
        endYearTextField.getStyleClass().add("textField");
        addInputListener(endYearTextField, true);
        grid.add(endYearTextField, 1, 8);

        HBox buttons = new HBox();

        Label nameErrorMessage = new Label();
        nameErrorMessage.getStyleClass().add("errorLabel");
        grid.add(nameErrorMessage,1,3);
        Label studentNumberErrorMessage = new Label();
        studentNumberErrorMessage.getStyleClass().add("errorLabel");
        grid.add(studentNumberErrorMessage,1,5);

        ToggleButton createButton = new ToggleButton("REGISTER");
        createButton.getStyleClass().add("blueButton");
        createButton.setOnAction(e -> {

            boolean notEmpty = true;

            if (nameTextField.getText().trim().isEmpty()) {
                nameTextField.getStyleClass().add("textFieldFalse");
                nameErrorMessage.setText("Invalid name");
                notEmpty = false;
            } else {
                nameTextField.getStyleClass().add("textFieldTrue");
            }

            if (studentNumberTextField.getText().trim().isEmpty()) {
                studentNumberTextField.getStyleClass().add("textFieldFalse");
                studentNumberErrorMessage.setText("Invalid student number");
                notEmpty = false;
            } else {
                studentNumberTextField.getStyleClass().add("textFieldTrue");
            }

            if (notEmpty) {

                // Create a new account

                stage.close();
            }
        });

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.getStyleClass().add("whiteButton");
        backButton.setOnAction( e -> stage.setScene(getStartScreen()) );

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(15);
        buttons.setPadding(new Insets(15,0,0,0));
        buttons.getChildren().addAll(backButton, createButton);

        grid.add(buttons, 1,10);
        buttons.getParent().requestFocus();

        return scene;
    }

    /**
     * Returns the top menu
     * @param student - Student class member
     * @return top menu
     */
    private HBox getTopMenu(Student student) {

        HBox menu = new HBox();

        ToggleButton studentInformationButton = new ToggleButton("Student information");
        studentInformationButton.getStyleClass().add("topMenuButton");
        studentInformationButton.setOnAction(e -> stage.setScene(getStudentInformationScreen(student)) );

        ToggleButton structureOfStudiesButton = new ToggleButton("Structure of studies");
        structureOfStudiesButton.getStyleClass().add("topMenuButton");
        structureOfStudiesButton.setOnAction( e -> stage.setScene(getStructureOfStudiesScreen(student)) );

        menu.getChildren().addAll(studentInformationButton, structureOfStudiesButton);

        return menu;
    }

    /**
     * Implements the main view of the program
     * @param student - Student class member
     */
    private void mainView(Student student) {

        stage.setTitle("SISU");

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(getTopMenu(student));
        borderPane.getTop().setStyle("-fx-background-color: #515151;");

        Scene scene = new Scene(borderPane, 600, 400);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns student information screen
     * @param student - Student class member
     * @return student information screen
     */
    private Scene getStudentInformationScreen(Student student) {

        return new Scene(new HBox());
    }

    /**
     * Returns structure of studies screen
     * @param student - Student class member
     * @return structure of studies screen
     */
    private Scene getStructureOfStudiesScreen(Student student) {

        return new Scene(new HBox());
    }
}