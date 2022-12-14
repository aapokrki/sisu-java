package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements a graphical user interface for checking studies in Sisu
 */
public class Sisu extends Application {

    private BorderPane mainWindow;
    private StudentData data;

    /**
     * Constructor
     */
    public Sisu() {
    }

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

        stage.setTitle("SISU");
        stage.getIcons().add(new Image("/TUNI-face.png"));

        // The whole program runs by changing Panes in the BorderPane
        mainWindow = new BorderPane();
        mainWindow.setCenter(getStartScreen());
        mainWindow.setBackground(Background.EMPTY);

        // Get custom style sheets
        Scene scene = new Scene(mainWindow);
        scene.setFill(Paint.valueOf("#ffffff"));
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/Sisu.css")).toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }

    /**
     * Calls to save data when program is closed
     */
    @Override
    public void stop() {
        data.save();
    }

    /**
     * Adds input listeners to text fields for correct inputs.
     * Used for all text fields in registration screen,login screen and student information edit screen
     * @param textField - text field
     * @param onlyIntegers - boolean value representing whether textField takes integers (years) or not
     */
    private void addInputListener(TextField textField, boolean onlyIntegers) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (onlyIntegers) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                    newValue = "";
                }
                if (textField.getText().length() > 4) {
                    String s = textField.getText().substring(0, 4);
                    textField.setText(s);

                    textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                } else {
                    textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), false);
                }
            }

            if (!onlyIntegers) {
                if (newValue.isBlank()) {
                    textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                } else {
                    textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), false);
                    textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                }
            }
        });
    }

    /**
     * Returns the start screen
     * @return start screen
     */
    private Pane getStartScreen() {

        Label title = new Label("Welcome to Sisu!");
        title.setId("welcomeLabel");
        title.setPadding(new Insets(0,0,30,0));

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.setId("whiteButton");
        loginButton.setOnAction( e -> {
            mainWindow.setCenter(getLoginScreen());
        } );

        ToggleButton registerButton = new ToggleButton("REGISTER");
        registerButton.setId("whiteButton");
        registerButton.setOnAction( e -> {
            mainWindow.setCenter(getRegistrationScreen());

            /*
            try {
                mainWindow.setCenter(getRegistrationScreen());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

             */
        });

        HBox buttons = new HBox(loginButton, registerButton);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(18);

        VBox vBox = new VBox(title, buttons);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(Background.EMPTY);
        vBox.setSpacing(50);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        title.requestFocus();

        return vBox;
    }

    /**
     * Returns the login screen
     * @return login screen
     */
    private Pane getLoginScreen() {
        GridPane grid = new GridPane();
        grid.setBackground(Background.EMPTY);

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

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.setId("whiteButton");
        backButton.setOnAction( e -> {
            mainWindow.setCenter(getStartScreen());
        } );

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
                mainWindow.setTop(getTopMenu(false));
                mainWindow.setCenter(getMainView());
            }
        });

        HBox buttons = new HBox(backButton, loginButton);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(18);

        grid.add(buttons, 1, 4);
        buttons.getParent().requestFocus();

        return grid;
    }

    /**
     * Returns the registration screen
     * @return registration screen
     */
    private Pane getRegistrationScreen() { //throws IOException

        GridPane grid = new GridPane();
        grid.setBackground(Background.EMPTY);
        grid.setVgap(5);
        grid.setHgap(48);
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

        Label nameErrorMessage = new Label();
        nameErrorMessage.setId("errorLabel");
        grid.add(nameErrorMessage,1,3);

        Label studentNumberLabel = new Label("Student number:");
        studentNumberLabel.setId("textLabel");
        grid.add(studentNumberLabel, 0, 4);

        TextField studentNumberTextField = new TextField();
        studentNumberTextField.setId("textField");
        addInputListener(studentNumberTextField, false);
        grid.add(studentNumberTextField, 1, 4);

        Label studentNumberErrorMessage = new Label();
        studentNumberErrorMessage.setId("errorLabel");
        grid.add(studentNumberErrorMessage,1,5);

        Label degreeProgrammeLabel = new Label("Select degree programme");
        degreeProgrammeLabel.setId("textLabel");
        grid.add(degreeProgrammeLabel,0,6);

        AtomicReference<String> inputDegreeProgramme = new AtomicReference<>(null);
        AtomicReference<String> inputMandatoryStudyModule = new AtomicReference<>(null);

        ComboBox<Object> studyModulesSelection = new ComboBox<>();
        studyModulesSelection.setPrefWidth(200);
        studyModulesSelection.setVisible(false);

        studyModulesSelection.setOnAction( e -> studyModulesSelection.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true));

        ComboBox<Object> degreeProgrammeSelection = new ComboBox<>();
        degreeProgrammeSelection.setPrefWidth(200);

        Map<String, String> degreeProgrammes = data.jsonData.getAllDegreeProgrammes();
        degreeProgrammeSelection.getItems().addAll(degreeProgrammes.keySet());
        degreeProgrammeSelection.setOnAction( e -> {

            degreeProgrammeSelection.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);

            String degreeProgrammeName = degreeProgrammeSelection.getSelectionModel().getSelectedItem().toString();
            String degreeProgrammeId = degreeProgrammes.get(degreeProgrammeName);

            inputDegreeProgramme.set(degreeProgrammeId);

            Map<String ,String> studyModules = null;
            studyModules = data.jsonData.getStudyModuleSelection(degreeProgrammeId);
            if (studyModules != null) {
                studyModulesSelection.getItems().clear();
                studyModulesSelection.getItems().addAll(studyModules.keySet());
                studyModulesSelection.getSelectionModel().selectFirst();
                studyModulesSelection.setVisible(true);
                studyModulesSelection.requestFocus();
            } else {
                studyModulesSelection.setVisible(false);
            }
        });

        HBox dropMenus = new HBox(degreeProgrammeSelection, studyModulesSelection);
        dropMenus.setSpacing(40);
        dropMenus.setPadding(new Insets(0,0,20,0));
        grid.add(dropMenus,0,7,2,1);

        Label startYearLabel = new Label("Start year (optional):");
        startYearLabel.setId("textLabel");
        GridPane.setMargin(startYearLabel, new Insets(0, 0, 12, 0));
        grid.add(startYearLabel, 0, 8);

        TextField startYearTextField = new TextField();
        startYearTextField.setId("textField");
        GridPane.setMargin(startYearTextField, new Insets(0, 0, 12, 0));
        addInputListener(startYearTextField, true);
        grid.add(startYearTextField, 1, 8);

        Label endYearLabel = new Label("Estimated end year (optional):");
        endYearLabel.setId("textLabel");
        grid.add(endYearLabel, 0, 9);

        TextField endYearTextField = new TextField();
        endYearTextField.setId("textField");
        addInputListener(endYearTextField, true);
        grid.add(endYearTextField, 1, 9);

        Label yearErrorMessage = new Label();
        yearErrorMessage.setId("errorLabel");
        grid.add(yearErrorMessage,1,10);

        ToggleButton createButton = new ToggleButton("REGISTER");
        createButton.setId("blueButton");
        createButton.setOnAction(e -> {

            String name = nameTextField.getText().trim();
            String studentNumber = studentNumberTextField.getText().trim().toUpperCase(Locale.ROOT);
            String startYear = startYearTextField.getText();
            String endYear = endYearTextField.getText();

            boolean fieldsNotEmpty = true;

            if (name.isEmpty()) {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                nameErrorMessage.setText("Invalid name");
                fieldsNotEmpty = false;
            } else {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                nameErrorMessage.setText("");
            }

            if (studentNumber.isEmpty()) {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                studentNumberErrorMessage.setText("Invalid student number");
                fieldsNotEmpty = false;
            } else {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                studentNumberErrorMessage.setText("");
            }

            if (inputDegreeProgramme.get() == null) {
                degreeProgrammeSelection.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                fieldsNotEmpty = false;
            } else if (studyModulesSelection.isVisible()) {

                String studyModuleId = null;
                studyModuleId = data.jsonData.getStudyModuleSelection(inputDegreeProgramme.get()).get(studyModulesSelection.getValue());

                inputMandatoryStudyModule.set(studyModuleId);
            }

            if (!startYear.isEmpty() && endYear.isEmpty()) {
                endYearTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                yearErrorMessage.setText("Both fields required");
            }
            if (startYear.isEmpty() && !endYear.isEmpty()) {
                startYearTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                yearErrorMessage.setText("Both fields required");
            }
            if (!startYear.isEmpty() && !endYear.isEmpty()) {
                startYearTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), false);
                endYearTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), false);
                yearErrorMessage.setText("");
            }

            if (fieldsNotEmpty) {
                try {
                    if (!data.createAccount(name, studentNumber, inputDegreeProgramme.get(), inputMandatoryStudyModule.get(), startYear, endYear)) {
                        studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                        studentNumberErrorMessage.setText("Account exists");
                    } else {
                        mainWindow.setTop(getTopMenu(false));
                        mainWindow.setCenter(getMainView());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ToggleButton backButton = new ToggleButton("BACK");
        backButton.setId("whiteButton");
        backButton.setOnAction( e -> mainWindow.setCenter(getStartScreen()));

        HBox buttons = new HBox(backButton, createButton);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(18);
        buttons.setPadding(new Insets(15,0,0,0));

        grid.add(buttons, 1,11);
        buttons.getParent().requestFocus();

        return grid;
    }

     /**
     * Returns the top menu
     * @param nameChanged - boolean value representing if user has edited their name and it should be changed on the top left corner of the menu
     * @return top menu
     */
    private HBox getTopMenu(boolean nameChanged) {

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
        if (nameChanged) {
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), true);
        }

        ToggleButton structureOfStudiesButton = new ToggleButton("Structure of studies");
        structureOfStudiesButton.setId("topMenuButton");

        mainViewButton.setOnAction(e -> {
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            mainWindow.setCenter(getMainView());
        });

        studentInformationButton.setOnAction(e -> {
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), true);
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            mainWindow.setCenter(getStudentInformationScreen());
        } );

        structureOfStudiesButton.setOnAction( e -> {
            structureOfStudiesButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), true);
            studentInformationButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("white"), false);
            mainWindow.setCenter(getStructureOfStudiesScreen());
        } );

        Line separator = new Line(0, 0, 0, 28);
        separator.setId("line");

        Label name = new Label(data.user.name);
        name.setId("textLabelBoldWhite");

        Label student = new Label("STUDENT");
        student.setId("textLabelSmallGray");

        VBox texts = new VBox(name, student);
        texts.setBackground(Background.EMPTY);
        texts.setPadding(new Insets(0,4,0,0));

        MenuButton nameButton = new MenuButton();
        nameButton.setId("topMenuButton2");
        nameButton.setGraphic(texts);
        MenuItem logOutButton = new MenuItem("Log out");
        nameButton.getItems().add(logOutButton);

        logOutButton.setOnAction( e -> {
            data.user = null;
            mainWindow.getChildren().remove(mainWindow.getTop());
            mainWindow.setCenter(getStartScreen());
        });

        HBox left = new HBox(mainViewButton, separator, studentInformationButton, structureOfStudiesButton);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        HBox right = new HBox(nameButton);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);

        menu.getChildren().addAll(left, right);

        return menu;
    }

    /**
     * Returns main view screen
     * @return main view screen
     */
    private Pane getMainView() {

        Image img = new Image("/Graduate.png");
        ImageView imv = new ImageView(img);
        imv.setPreserveRatio(true);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(40, 70, 40, 70));
        vBox.setId("mainView");
        vBox.setAlignment(Pos.CENTER_RIGHT);

        Label gpaTitle = new Label("Average grade");
        gpaTitle.setId("textLabelWhite");
        gpaTitle.setPadding(new Insets(15,15,0,15));
        Label gpa = new Label(String.format("%.2f",data.user.getGPA()));
        gpa.setId("numberLabel");
        gpa.setPadding(new Insets(0,15,15,15));

        VBox gpaBox = new VBox(gpaTitle, gpa);
        gpaBox.setId("gpaBox");
        gpaBox.setAlignment(Pos.BOTTOM_CENTER);

        Label creditTitle = new Label("Study credits");
        creditTitle.setId("textLabelWhite");
        creditTitle.setPadding(new Insets(15,15,0,15));
        Label credit = new Label(String.format("%d/%d",data.user.getStudyCredits(), data.user.getDegreeProgramme().getMinCredits()));
        credit.setId("numberLabel");
        credit.setPadding(new Insets(0,15,15,15));

        VBox creditBox = new VBox(creditTitle,credit);
        creditBox.setId("creditBox");
        creditBox.setAlignment(Pos.BOTTOM_CENTER);

        HBox dataBox = new HBox(gpaBox,creditBox);
        dataBox.setSpacing(10);

        Label title = new Label("Welcome to Sisu!");
        title.setId("welcomeLabel");
        title.setPadding(new Insets(60,0,0,0));

        HBox left = new HBox(title);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        HBox right = new HBox(dataBox);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox hBox = new HBox(left, right);

        Label mainViewTitle = new Label("Main view");
        mainViewTitle.setId("titleLabel");
        Label mainViewInfo = new Label("Check your GPA and amount of study credits");
        mainViewInfo.setId("infoBox");
        VBox mainViewBox = new VBox(mainViewTitle, mainViewInfo);
        mainViewInfo.setAlignment(Pos.TOP_LEFT);

        Label studentInformationTitle = new Label("Student information");
        studentInformationTitle.setId("titleLabel");
        Label studentInformationInfo = new Label("Check your personal info and edit it");
        studentInformationInfo.setId("infoBox");
        VBox studentInformationBox = new VBox(studentInformationTitle, studentInformationInfo);
        studentInformationInfo.setAlignment(Pos.TOP_LEFT);

        Label structureOfStudiesTitle = new Label("Structure of studies");
        structureOfStudiesTitle.setId("titleLabel");
        Label structureOfStudiesInfo = new Label("Check your studies and edit your progress");
        structureOfStudiesInfo.setId("infoBox");
        VBox structureOfStudiesInfoBox = new VBox(structureOfStudiesTitle, structureOfStudiesInfo);
        structureOfStudiesInfo.setAlignment(Pos.TOP_LEFT);

        HBox infoBox = new HBox(mainViewBox, studentInformationBox, structureOfStudiesInfoBox);
        infoBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(mainViewBox, Priority.ALWAYS);
        HBox.setHgrow(studentInformationBox, Priority.ALWAYS);
        HBox.setHgrow(structureOfStudiesInfoBox, Priority.ALWAYS);
        infoBox.setSpacing(30);
        infoBox.setPadding(new Insets(70, 0, 20, 0));

        vBox.getChildren().addAll(hBox, infoBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        VBox.setVgrow(infoBox, Priority.ALWAYS);

        return vBox;
    }

    /**
     * Returns student information screen
     * @return student information screen
     */
    private Pane getStudentInformationScreen() {

        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #ffffff;");
        hBox.setPadding(new Insets(30, 60, 10, 60));
        hBox.setSpacing(50);

        Separator separator = new Separator(Orientation.VERTICAL);

        VBox editBox = new VBox();

        Label editTitle = new Label("Edit information");
        editTitle.setId("welcomeLabel");
        editTitle.setPadding(new Insets(0,0,30,0));

        Label editNameLabel = new Label("Name");
        editNameLabel.setId("titleLabelSmaller");
        TextField editNameTextField = new TextField(data.user.getName());
        editNameTextField.setId("textField");

        GridPane editYears = new GridPane();
        editYears.setPadding(new Insets(120,0,60,0));
        editYears.setBackground(Background.EMPTY);
        editYears.setHgap(30);

        Label editDegreeProgramme = new Label("Degree Programme");
        editDegreeProgramme.setId("titleLabelSmaller");

        Label editStartYearLabel = new Label("Start year");
        editStartYearLabel.setId("titleLabelSmaller");
        editYears.add(editStartYearLabel,0,0);
        String startYear = String.valueOf(data.user.getStartYear());
        if (data.user.getEndYear() == 0) {
            startYear = "-";
        }
        TextField editStartYearTextField = new TextField(String.valueOf(data.user.getStartYear()));
        addInputListener(editStartYearTextField, true);
        editStartYearTextField.setId("textField");
        editYears.add(editStartYearTextField,0,1);

        Label editEndYearLabel = new Label("End year (estimated)");
        editEndYearLabel.setId("titleLabelSmaller");
        editYears.add(editEndYearLabel,1,0);

        String endYear = String.valueOf(data.user.endYear);
        if (data.user.endYear == 0) {
            endYear = "-";
        }
        TextField editEndYearTextField = new TextField(String.valueOf(data.user.getEndYear()));
        addInputListener(editEndYearTextField, true);
        editEndYearTextField.setId("textField");
        editYears.add(editEndYearTextField,1,1);

        ToggleButton editButton = new ToggleButton("EDIT INFORMATION");
        editButton.setId("blueButton");

        ToggleButton deleteButton = new ToggleButton("DELETE ACCOUNT");
        deleteButton.setId("whiteButton");

        HBox buttons = new HBox(deleteButton, editButton);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.setSpacing(18);

        editButton.setOnAction( e -> {
            hBox.getChildren().addAll(separator, editBox);
            buttons.getChildren().clear();
        });

        deleteButton.setOnAction( e -> {
            data.deleteAccount();
            mainWindow.getChildren().remove(mainWindow.getTop());
            mainWindow.setCenter(getLoginScreen());
        });

        ToggleButton cancelButton = new ToggleButton("CANCEL");
        cancelButton.setId("whiteButton");
        cancelButton.setOnAction( e -> {
            editNameTextField.setText(data.user.getName());
            editStartYearTextField.setText(Integer.toString(data.user.getStartYear()));
            editEndYearTextField.setText(Integer.toString(data.user.getEndYear()));
            hBox.getChildren().removeAll(separator, editBox);
            buttons.getChildren().addAll(deleteButton, editButton);
        });

        ToggleButton doneButton = new ToggleButton("DONE");
        doneButton.setId("blueButton");
        doneButton.setOnAction( e -> {
            String newName = editNameTextField.getText().trim();
            String newStartYear = editStartYearTextField.getText().trim();
            String newEndYear = editEndYearTextField.getText().trim();

            if (!newStartYear.isEmpty() && !newEndYear.isEmpty()) {
                data.user.setStartYear(Integer.parseInt(newStartYear));
                data.user.setEndYear(Integer.parseInt(newEndYear));
            }
            if (!newName.equals(data.user.getName())) {
                data.user.setName(newName);
                mainWindow.setTop(getTopMenu(true));
            }
            mainWindow.setCenter(getStudentInformationScreen());
        });

        HBox editButtons = new HBox(cancelButton, doneButton);
        editButtons.setAlignment(Pos.BOTTOM_RIGHT);
        editButtons.setSpacing(18);

        editBox.getChildren().addAll(editTitle, editNameLabel, editNameTextField, editYears, editButtons);

        Label title = new Label("Student information");
        title.setId("welcomeLabel");
        title.setPadding(new Insets(0,0,30,0));

        Label nameTitle = new Label("Name");
        nameTitle.setId("titleLabelSmaller");
        Label name = new Label(data.user.name);
        name.setId("textLabelBigger");
        VBox nameBox = new VBox(nameTitle,name);
        nameBox.setPadding(new Insets(0,0,30,0));

        Label studentNumberTitle = new Label("Student number");
        studentNumberTitle.setId("titleLabelSmaller");
        Label studentNumber = new Label(data.user.studentNumber);
        studentNumber.setId("textLabelBigger");

        VBox studentNumberBox = new VBox(studentNumberTitle, studentNumber);
        studentNumberBox.setPadding(new Insets(0,0,30,0));

        GridPane years = new GridPane();
        years.setPadding(new Insets(0,0,60,0));
        years.setBackground(Background.EMPTY);
        years.setHgap(100);

        Label startYearTitle = new Label("Start year");
        startYearTitle.setId("titleLabelSmaller");
        years.add(startYearTitle, 0,0,1,1);

        Label startYearLabel = new Label(startYear);
        startYearLabel.setId("textLabelBigger");
        years.add(startYearLabel, 0,1,1,1);

        Label endYearTitle = new Label("End year (estimated)");
        endYearTitle.setId("titleLabelSmaller");
        years.add(endYearTitle, 1,0,1,1);

        Label endYearLabel = new Label(endYear);
        endYearLabel.setId("textLabelBigger");
        years.add(endYearLabel, 1,1,1,1);

        VBox informationBox = new VBox(title, nameBox, studentNumberBox, years, buttons);
        hBox.getChildren().add(informationBox);

        return hBox;
    }

    /**
     * Returns a graduate box for the course box which marks the course as completed
     * @param courseUnit - course
     * @return graduate box
     */
    private VBox getGraduateBox(CourseUnit courseUnit) {

        boolean courseCompleted = courseUnit.isCompleted();

        String gradeStr = "";
        if (courseCompleted) {
            int grade = courseUnit.getGrade();
            gradeStr = String.valueOf(grade);
            if (grade == 0) {
                gradeStr = "Pass";
            }
        }

        Image img = new Image("/Graduate.png");
        ImageView imv = new ImageView(img);
        imv.setPreserveRatio(true);
        imv.setFitHeight(12);
        Label courseGradeTxt = new Label(gradeStr);
        courseGradeTxt.setId("textLabel");
        VBox graduateBox = new VBox(imv, courseGradeTxt);
        graduateBox.setSpacing(5);
        graduateBox.setAlignment(Pos.CENTER);
        graduateBox.setId("complete-box");

        return graduateBox;
    }

    /**
     * Returns single course box for the structure of studies screen
     * @param courseUnit - course
     * @return course box visualizing a course
     */
    private HBox getCourseBox(CourseUnit courseUnit) {

        HBox courseBox = new HBox();
        courseBox.setPadding(new Insets(0,10,0,0));

        boolean courseCompleted = courseUnit.isCompleted();

        Label courseCreditNum = new Label(courseUnit.getCredits());
        courseCreditNum.setId("textLabelBold");
        Label courseCreditTxt = new Label("cr");
        courseCreditTxt.setId("textLabel");
        VBox courseCreditsBox = new VBox(courseCreditNum, courseCreditTxt);
        courseCreditsBox.setAlignment(Pos.CENTER);
        courseCreditsBox.setId("credit-box");

        VBox graduateBox = getGraduateBox(courseUnit);

        Label courseId = new Label(courseUnit.getCode());
        courseId.setId("textLabel");
        Label courseName = new Label(courseUnit.getName());
        courseName.setId("textLabelBold");
        VBox courseNameBox = new VBox(courseId, courseName);
        courseNameBox.setPadding(new Insets(0,0,0,10));
        courseNameBox.setAlignment(Pos.CENTER_LEFT);

        TextField courseGradeTextField = new TextField();
        courseGradeTextField.setId("textFieldGrade");
        courseGradeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[0-5]$")) {
                Platform.runLater(courseGradeTextField::clear);
            }
        });

        if (courseCompleted) {
            courseBox.getChildren().addAll(courseCreditsBox, graduateBox, courseNameBox);
        } else {
            courseBox.getChildren().addAll(courseCreditsBox, courseNameBox, courseGradeTextField);
        }

        courseGradeTextField.setOnAction( e -> {
            if (courseGradeTextField.getText().isEmpty()) {
                return;
            }
            int grade = Integer.parseInt(courseGradeTextField.getText());
            courseUnit.setGrade(grade);
            data.user.addCompletedCourse(courseUnit);

            VBox newGraduateBox = getGraduateBox(courseUnit);

            courseBox.getChildren().add(1, newGraduateBox);
            courseGradeTextField.setVisible(false);
        });

        courseBox.setAlignment(Pos.CENTER);
        courseBox.setId("course-box");

        HBox.setHgrow(courseNameBox, Priority.ALWAYS);

        return courseBox;
    }

    /**
     * Returns structure of studies screen
     * @return structure of studies screen
     */
    private Pane getStructureOfStudiesScreen() {

        Label title = new Label("Structure of studies");
        title.setId("welcomeLabel");
        title.setAlignment(Pos.BOTTOM_LEFT);

        Label info = new Label("Mark courses complete by typing grades into text fields.\nGrades 1-5, 0 = Pass");
        info.setId("textLabel");
        info.setAlignment(Pos.BOTTOM_RIGHT);

        HBox left = new HBox(title);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);

        HBox right = new HBox(info);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox texts = new HBox(left, right);

        Accordion degreeProgrammeAccordion = getStudiesStructure(data.user.getDegreeProgramme(), null);

        ScrollPane scrollPane = new ScrollPane(degreeProgrammeAccordion);
        scrollPane.setBackground(Background.EMPTY);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox vBox = new VBox(texts, scrollPane);
        vBox.setBackground(Background.EMPTY);

        vBox.setPadding(new Insets(30,60,10,60));
        vBox.setSpacing(20);

        return vBox;
    }

    /**
     * Lays course boxes in tidy order
     * @param studyModule - study module
     * @return HBox containing course boxes
     */
    private HBox getCourses(StudyModule studyModule) {

        VBox coursesBoxLeft = new VBox();
        coursesBoxLeft.setSpacing(10);
        coursesBoxLeft.setPrefWidth(500);
        VBox coursesBoxRight = new VBox();
        coursesBoxRight.setSpacing(10);
        coursesBoxRight.setPrefWidth(500);

        int counter = 0;
        int coursesCount = studyModule.getCourseUnits().size();

        for (CourseUnit courseUnit : studyModule.getCourseUnits()) {

            HBox courseBox = getCourseBox(courseUnit);

            if (counter <= coursesCount/2) {
                coursesBoxLeft.getChildren().add(courseBox);
            } else {
                coursesBoxRight.getChildren().add(courseBox);
            }
            ++counter;
        }
        HBox coursesBox = new HBox(coursesBoxLeft, coursesBoxRight);
        coursesBox.setSpacing(40);
        coursesBox.setPadding(new Insets(0,0,5,40));

        return coursesBox;
    }

    /**
     * Recursive function for adding studies structure view
     * @param degreeProgramme - degree programme
     * @param studyModule - study module
     * @return Accordion containing studies structure
     */
    private Accordion getStudiesStructure(DegreeProgramme degreeProgramme, StudyModule studyModule) {

        Accordion accordion = new Accordion();

        if (degreeProgramme != null && !degreeProgramme.getStudyModules().isEmpty()) {
            for (StudyModule studyModule1 : degreeProgramme.getStudyModules()) {

                Accordion accordion1 = getStudiesStructure(null, studyModule1);
                accordion1.setPadding(new Insets(0, 0, 0, 40));

                TitledPane studyModulePane = new TitledPane(studyModule1.getName(), accordion1);
                accordion.getPanes().add(studyModulePane);
            }
        }

        if (studyModule != null) {

            for (StudyModule studyModule1 : studyModule.getStudyModules()) {

                VBox vBox = new VBox();

                if (!studyModule1.getCourseUnits().isEmpty()) {
                    vBox.getChildren().add(getCourses(studyModule1));
                }

                Accordion accordion1 = getStudiesStructure(null, studyModule1);
                accordion1.setPadding(new Insets(0, 0, 0, 40));
                vBox.getChildren().add(accordion1);

                TitledPane studyModulePane = new TitledPane(studyModule1.getName(), vBox);
                accordion.getPanes().add(studyModulePane);
            }
        }
        return accordion;
    }
}