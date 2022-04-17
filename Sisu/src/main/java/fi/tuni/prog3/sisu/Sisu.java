package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Implements a graphical user interface for checking studies in Sisu
 */
public class Sisu extends Application {

    private Stage stage;
    private final String style = this.getClass().getResource("/Sisu.css").toExternalForm();

    private StudentData data;

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

        // Get data from JSON to program
        data = new StudentData();

        this.stage = stage;
        this.stage.setTitle("SISU");
        this.stage.getIcons().add(new Image("/TUNI-face.png"));
        Scene startScene = getStartScreen();

        //Scene startScene = getMainScene();
        stage.setScene(startScene);
        stage.show();
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
                textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
            } else {
                textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
            }
        });
    }

    /**
     * Returns the start screen
     * @return start screen
     */
    private Scene getStartScreen() {

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox,450,350);
        vBox.setBackground(Background.EMPTY);
        scene.setFill(Paint.valueOf("#ffffff"));

        scene.getStylesheets().add(this.style);

        vBox.setSpacing(20);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Welcome to Sisu!");
        title.setId("welcomeLabel");

        HBox buttons = new HBox();

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.setId("whiteButton");
        loginButton.setOnAction( e -> stage.setScene(getLoginScreen()) );

        ToggleButton registerButton = new ToggleButton("REGISTER");
        registerButton.setId("whiteButton");
        registerButton.setOnAction( e -> stage.setScene(getRegistrationScreen()));

        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(18);
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
        Scene scene = new Scene(grid, 450, 350);
        grid.setBackground(Background.EMPTY);
        scene.setFill(Paint.valueOf("#ffffff"));
        scene.getStylesheets().add(this.style);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);


        Label title = new Label("Login by entering student number");
        title.setId("titleLabel");

        grid.add(title,0,0,2,1);

        Label studentNumberLabel = new Label("Student number:");
        studentNumberLabel.setId("textLabel");
        grid.add(studentNumberLabel,0,2);

        TextField studentNumberTextField = new TextField();
        studentNumberTextField.setId("textField");
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField,1,2);

        Label errorMessage = new Label();
        errorMessage.setId("errorLabel");
        grid.add(errorMessage,1,3);

        HBox buttons = new HBox();

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.setId("whiteButton");
        backButton.setOnAction( e -> stage.setScene(getStartScreen()) );

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.setId("blueButton");
        loginButton.setOnAction( e -> {

            String studentNumber = studentNumberTextField.getText().trim().toUpperCase(Locale.ROOT);

            if (studentNumber.isEmpty()) {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                errorMessage.setText("Invalid student number");
            } else if (!data.login(studentNumber)) {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);

                errorMessage.setText("Account does not exist");
            } else {
                stage.setScene(getMainScene());
            }
        });

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(18);
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
        Scene scene = new Scene(grid,450,350);
        grid.setBackground(Background.EMPTY);
        scene.setFill(Paint.valueOf("#ffffff"));
        scene.getStylesheets().add(this.style);

        grid.setVgap(5);
        grid.setHgap(20);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        Label title = new Label("Register a profile");
        title.setId("titleLabel");
        grid.add(title, 0, 0, 2, 1);

        Label nameLabel = new Label("Name:");
        nameLabel.setId("textLabel");
        grid.add(nameLabel, 0, 2);

        TextField nameTextField = new TextField();
        nameTextField.setId("textField");
        addInputListener(nameTextField, false);
        grid.add(nameTextField, 1, 2);

        Label studentNumberLabel = new Label("Student number:");
        studentNumberLabel.setId("textLabel");
        grid.add(studentNumberLabel, 0, 4);

        TextField studentNumberTextField = new TextField();
        studentNumberTextField.setId("textField");
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField, 1, 4);

        Label startYearLabel = new Label("Start year (optional):");
        startYearLabel.setId("textLabel");
        GridPane.setMargin(startYearLabel, new Insets(0, 0, 12, 0));
        grid.add(startYearLabel, 0, 6);


        TextField startYearTextField = new TextField();
        startYearTextField.setId("textField");
        GridPane.setMargin(startYearTextField, new Insets(0, 0, 12, 0));
        addInputListener(startYearTextField, true);
        grid.add(startYearTextField, 1, 6);

        Label endYearLabel = new Label("Estimated end year (optional):");
        endYearLabel.setId("textLabel");
        grid.add(endYearLabel, 0, 8);

        TextField endYearTextField = new TextField();
        endYearTextField.setId("textField");
        addInputListener(endYearTextField, true);
        grid.add(endYearTextField, 1, 8);

        HBox buttons = new HBox();

        Label nameErrorMessage = new Label();
        nameErrorMessage.setId("errorLabel");
        grid.add(nameErrorMessage,1,3);
        Label studentNumberErrorMessage = new Label();
        studentNumberErrorMessage.setId("errorLabel");
        grid.add(studentNumberErrorMessage,1,5);

        ToggleButton createButton = new ToggleButton("REGISTER");
        createButton.setId("blueButton");
        createButton.setOnAction(e -> {

            String name = nameTextField.getText().trim();
            String studentNumber = studentNumberTextField.getText().trim().toUpperCase(Locale.ROOT);
            String startYear = startYearTextField.getText();
            String endYear = endYearTextField.getText();

            ArrayList<String> textFieldData = new ArrayList<>();

            boolean notEmpty = true;

            if (name.isEmpty()) {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                nameErrorMessage.setText("Invalid name");
                notEmpty = false;
            } else {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                textFieldData.add(name);
            }

            if (studentNumber.isEmpty()) {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                studentNumberErrorMessage.setText("Invalid student number");
                notEmpty = false;
            } else {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                textFieldData.add(studentNumber);
            }

            if (!startYear.isEmpty()) {
                textFieldData.add(startYear);
                if (!endYear.isEmpty()) {
                    textFieldData.add(endYear);
                }
            }

            if (notEmpty) {
                if (!data.createAccount(textFieldData)) {
                    studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                    studentNumberErrorMessage.setText("Account exists");
                } else {
                    stage.setScene(getMainScene());
                }
            }
        });

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.setId("whiteButton");
        backButton.setOnAction( e -> stage.setScene(getStartScreen()) );

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(18);
        buttons.setPadding(new Insets(15,0,0,0));
        buttons.getChildren().addAll(backButton, createButton);

        grid.add(buttons, 1,10);
        buttons.getParent().requestFocus();

        return scene;
    }

    /**
     * Returns the top menu
     * @return top menu
     */
    private HBox getTopMenu(BorderPane borderPane) {

        HBox menu = new HBox();
        menu.setId("topMenu");

        Image img = new Image("/SISU-logo.png");
        ImageView imv = new ImageView(img);
        imv.setPreserveRatio(true);
        imv.setFitHeight(18);
        ToggleButton mainViewButton = new ToggleButton();
        mainViewButton.setGraphic(imv);
        mainViewButton.setId("topMenuButton");

        ToggleButton studentInformationButton = new ToggleButton("Student information");
        studentInformationButton.setId("topMenuButton");

        ToggleButton structureOfStudiesButton = new ToggleButton("Structure of studies");
        structureOfStudiesButton.setId("topMenuButton");

        mainViewButton.setOnAction(e -> {
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            borderPane.setCenter(getMainView());
        });

        studentInformationButton.setOnAction(e -> {
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), true);
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            borderPane.setCenter(getStudentInformationScreen());
        } );

        structureOfStudiesButton.setOnAction( e -> {
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), true);
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            borderPane.setCenter(getStructureOfStudiesScreen());
        } );

        /*
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setId("separator");
        separator.setHalignment(HPos.RIGHT);
        */

        Line separator = new Line(0, 0, 0, 28);
        separator.setId("line");

        menu.getChildren().addAll(mainViewButton, separator, studentInformationButton, structureOfStudiesButton);
        menu.setAlignment(Pos.CENTER_LEFT);



        return menu;
    }

    /**
     * Implements the main window scene
     * @return main window scene
     */
    private Scene getMainScene() {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(this.style);

        borderPane.setTop(getTopMenu(borderPane));
        borderPane.setCenter(getMainView());
        return scene;
    }

    /**
     * Returns main view screen
     * @return main view screen
     */
    private Pane getMainView() {

        VBox vBox = new VBox();
        vBox.setBackground(Background.EMPTY);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Main view!");
        title.setId("welcomeLabel");

        vBox.getChildren().add(title);

        return vBox;
    }

    /**
     * Returns student information screen
     * @return student information screen
     */
    private Pane getStudentInformationScreen() {

        VBox vBox = new VBox();
        vBox.setBackground(Background.EMPTY);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Student information!");
        title.setId("welcomeLabel");

        vBox.getChildren().add(title);

        return vBox;
    }

    /**
     * Returns structure of studies screen
     * @return structure of studies screen
     */
    private Pane getStructureOfStudiesScreen() {

        VBox vBox = new VBox();
        vBox.setBackground(Background.EMPTY);

        vBox.setPadding(new Insets(10, 10, 10, 10));

        Label title = new Label("Structure of studies!");
        title.setId("welcomeLabel");

        vBox.getChildren().add(title);

        return vBox;
    }
}