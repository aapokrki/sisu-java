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

    private Stage stage;
    private BorderPane mainWindow;
    private final String style = this.getClass().getResource("/Sisu.css").toExternalForm();

    private StudentData data;

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

        this.stage = stage;
        this.stage.setTitle("SISU");
        this.stage.getIcons().add(new Image("/TUNI-face.png"));

        mainWindow = new BorderPane();
        mainWindow.setCenter(getStartScreen());
        mainWindow.setBackground(Background.EMPTY);

        Scene scene = new Scene(mainWindow);
        scene.setFill(Paint.valueOf("#ffffff"));
        scene.getStylesheets().add(this.style);

        //Scene startScene = getMainScene();
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
            if (onlyIntegers && textField.getText().length() > 4) {
                String s = textField.getText().substring(0, 4);
                textField.setText(s);
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
    private Pane getStartScreen() {

        Label title = new Label("Welcome to Sisu!");
        title.setId("welcomeLabel");

        ToggleButton loginButton = new ToggleButton("LOGIN");
        loginButton.setId("whiteButton");
        loginButton.setOnAction( e -> {
            mainWindow.setCenter(getLoginScreen());
        } );

        ToggleButton registerButton = new ToggleButton("REGISTER");
        registerButton.setId("whiteButton");
        registerButton.setOnAction( e -> {
            try {
                mainWindow.setCenter(getRegistrationScreen());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        HBox buttons = new HBox(loginButton, registerButton);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setSpacing(18);

        VBox vBox = new VBox(title, buttons);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(Background.EMPTY);
        vBox.setSpacing(20);
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
    private Pane getRegistrationScreen() throws IOException {

        ArrayList<String> textFieldData = new ArrayList<>();

        GridPane grid = new GridPane();
        grid.setBackground(Background.EMPTY);

        grid.setVgap(5);
        grid.setHgap(48);
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

        Label degreeProgrammeLabel = new Label("Select degree programme");
        degreeProgrammeLabel.setId("textLabel");

        MenuButton degreeProgrammeMenu = new MenuButton();
        degreeProgrammeMenu.setGraphic(degreeProgrammeLabel);
        degreeProgrammeMenu.setId("menuButton");
        degreeProgrammeMenu.setPrefWidth(200);

        ChoiceBox<String> studyModulesChoice = new ChoiceBox<>();
        studyModulesChoice.setPrefWidth(200);
        studyModulesChoice.setVisible(false);

        HBox dropMenus = new HBox(degreeProgrammeMenu, studyModulesChoice);
        dropMenus.setSpacing(40);
        dropMenus.setPadding(new Insets(20,0,20,0));
        grid.add(dropMenus,0,5,2,1);

        Map<String, String> degreeProgrammes = data.jsonData.getAllDegreeProgrammes();

        AtomicReference<String> inputDegreeProgramme = new AtomicReference<>(null);
        AtomicReference<String> inputMandatoryStudyModule = new AtomicReference<>(null);

        for (String degreeProgramme : degreeProgrammes.keySet()) {
            MenuItem degreeProgramItem = new MenuItem(degreeProgramme);
            degreeProgrammeMenu.getItems().add(degreeProgramItem);

            degreeProgramItem.setOnAction( e -> {
                degreeProgrammeMenu.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                degreeProgrammeLabel.setText(degreeProgramme);

                inputDegreeProgramme.set(degreeProgrammes.get(degreeProgramme));

                studyModulesChoice.getItems().clear();

                Map<String, String > studyModules = null;
                try {
                    studyModules = data.jsonData.getStudyModuleSelection(inputDegreeProgramme.get());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (studyModules != null) {
                    studyModulesChoice.getItems().addAll(studyModules.keySet());
                    studyModulesChoice.getSelectionModel().selectFirst();
                    studyModulesChoice.setVisible(true);
                } else {
                    studyModulesChoice.setVisible(false);
                }
            });
        }

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

            boolean notEmpty = true;

            if (name.isEmpty()) {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                nameErrorMessage.setText("Invalid name");
                notEmpty = false;
            } else {
                nameTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                //textFieldData.add(name);
            }

            if (studentNumber.isEmpty()) {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                //studentNumberErrorMessage.setText("Invalid student number");
                notEmpty = false;
            } else {
                studentNumberTextField.pseudoClassStateChanged(PseudoClass.getPseudoClass("true"), true);
                textFieldData.add(studentNumber);
            }

            if (inputDegreeProgramme.get() == null) {
                degreeProgrammeMenu.pseudoClassStateChanged(PseudoClass.getPseudoClass("false"), true);
                notEmpty = false;
            } else if (studyModulesChoice.isVisible()) {

                String studyModuleId = null;
                try {
                    studyModuleId = data.jsonData.getStudyModuleSelection(inputDegreeProgramme.get()).get(studyModulesChoice.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                inputMandatoryStudyModule.set(studyModuleId);
            }

            /*
            if (!startYear.isEmpty()) {
                //textFieldData.add(startYear);
                if (!endYear.isEmpty()) {
                    //textFieldData.add(endYear);
                }
            }
            */

            if (notEmpty) {
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

        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setSpacing(18);
        buttons.setPadding(new Insets(15,0,0,0));
        buttons.getChildren().addAll(backButton, createButton);

        grid.add(buttons, 1,10);
        buttons.getParent().requestFocus();

        return grid;
    }

    /**
     * Returns the top menu
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
        name.setId("textLabelWhite");

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
        left.getChildren().addAll();
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

        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #ffffff;");
        hBox.setPadding(new Insets(30, 60, 10, 60));
        hBox.setSpacing(50);

        Separator separator = new Separator(Orientation.VERTICAL);

        VBox editBox = new VBox();

        Label editTitle = new Label("Edit information");
        editTitle.setId("welcomeLabel");

        Label editNameLabel = new Label("Name");
        editNameLabel.setId("smallerTitleLabel");
        TextField editNameTextField = new TextField(data.user.getName());
        editNameTextField.setId("textField");

        GridPane editYears = new GridPane();
        editYears.setPadding(new Insets(120,0,60,0));
        editYears.setBackground(Background.EMPTY);
        editYears.setHgap(30);

        Label editStartYearLabel = new Label("Start year");
        editStartYearLabel.setId("smallerTitleLabel");
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
        editEndYearLabel.setId("smallerTitleLabel");
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

        Label nameTitle = new Label("Name");
        nameTitle.setId("smallerTitleLabel");
        Label name = new Label(data.user.name);
        name.setId("textLabelBigger");
        VBox nameBox = new VBox(nameTitle,name);
        nameBox.setPadding(new Insets(0,0,30,0));

        Label studentNumberTitle = new Label("Student number");
        studentNumberTitle.setId("smallerTitleLabel");
        Label studentNumber = new Label(data.user.studentNumber);
        studentNumber.setId("textLabelBigger");

        VBox studentNumberBox = new VBox(studentNumberTitle, studentNumber);
        studentNumberBox.setPadding(new Insets(0,0,30,0));

        GridPane years = new GridPane();
        years.setPadding(new Insets(0,0,60,0));
        years.setBackground(Background.EMPTY);
        years.setHgap(100);

        Label startYearTitle = new Label("Start year");
        startYearTitle.setId("smallerTitleLabel");
        years.add(startYearTitle, 0,0,1,1);

        Label startYearLabel = new Label(startYear);
        startYearLabel.setId("textLabelBigger");
        years.add(startYearLabel, 0,1,1,1);

        Label endYearTitle = new Label("End year (estimated)");
        endYearTitle.setId("smallerTitleLabel");
        years.add(endYearTitle, 1,0,1,1);

        Label endYearLabel = new Label(endYear);
        endYearLabel.setId("textLabelBigger");
        years.add(endYearLabel, 1,1,1,1);

        VBox informationBox = new VBox(title, nameBox, studentNumberBox, years, buttons);
        hBox.getChildren().add(informationBox);

        return hBox;
    }

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

        /*
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

        */

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

        /*
        Accordion degreeProgrammeAccordion = new Accordion();
        Accordion studyModules1Accordion = new Accordion();
        Accordion studyModules2Accordion = new Accordion();

        for (StudyModule studyModule1 : data.user.degreeProgramme.getStudyModules()) {

            for (StudyModule studyModule2 : studyModule1.getStudyModules()) {

                VBox coursesBoxLeft = new VBox();
                coursesBoxLeft.setSpacing(10);
                coursesBoxLeft.setPrefWidth(500);
                VBox coursesBoxRight = new VBox();
                coursesBoxRight.setSpacing(10);
                coursesBoxRight.setPrefWidth(500);

                int counter = 0;
                int coursesCount = studyModule2.getCourseUnits().size();

                for (CourseUnit courseUnit : studyModule2.getCourseUnits()) {

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
                TitledPane coursesPane = new TitledPane(studyModule2.getName(), coursesBox);
                studyModules2Accordion.getPanes().add(coursesPane);
            }
            HBox studyModules2Box = new HBox(studyModules2Accordion);
            studyModules2Box.setPadding(new Insets(0,0,0,40));
            TitledPane studyModulePane = new TitledPane(studyModule1.getName(), studyModules2Box);
            studyModules1Accordion.getPanes().add(studyModulePane);
        }
        HBox studyModules1Box = new HBox(studyModules1Accordion);
        studyModules1Box.setPadding(new Insets(0,0,0,40));
        TitledPane degreeProgramPane = new TitledPane(data.user.degreeProgramme.getName(), studyModules1Box);
        degreeProgrammeAccordion.getPanes().add(degreeProgramPane);


        */
        Accordion degreeProgrammeAccordion = getStudiesStructure(new Accordion(), data.user.getDegreeProgramme(), null);
        ScrollPane scrollPane = new ScrollPane(degreeProgrammeAccordion);
        scrollPane.setBackground(Background.EMPTY);

        VBox vBox = new VBox(title, scrollPane);
        vBox.setBackground(Background.EMPTY);

        vBox.setPadding(new Insets(30,60,10,60));

        return vBox;
    }

    private Accordion getStudiesStructure(Accordion accordion, DegreeProgramme degreeProgramme, StudyModule studyModule) {

        //Accordion newAccordion = new Accordion();

        // Degree programmes

        if (degreeProgramme != null && degreeProgramme.getDegreeProgrammes() != null) {
            for (DegreeProgramme degreeProgramme1 : degreeProgramme.getDegreeProgrammes()) {
                HBox hBox = new HBox(getStudiesStructure(new Accordion(), degreeProgramme1, null));
                hBox.setPadding(new Insets(0,0,0,40));

                TitledPane studyModulePane = new TitledPane(degreeProgramme1.getName(), hBox);
                accordion.getPanes().add(studyModulePane);
            }
        }

        // Study modules

        if (degreeProgramme != null && degreeProgramme.getStudyModules() != null) {
            for (StudyModule studyModule1 : degreeProgramme.getStudyModules()) {

                HBox hBox = new HBox(getStudiesStructure(new Accordion(), null, studyModule1));
                hBox.setPadding(new Insets(0, 0, 0, 40));

                TitledPane studyModulePane = new TitledPane(studyModule1.getName(), hBox);
                accordion.getPanes().add(studyModulePane);
            }
        }

        if (studyModule != null && studyModule.getStudyModules() != null) {
            for (StudyModule studyModule1 : studyModule.getStudyModules()) {

                HBox hBox = new HBox(getStudiesStructure(new Accordion(), null, studyModule1));
                hBox.setPadding(new Insets(0, 0, 0, 40));

                TitledPane studyModulePane = new TitledPane(studyModule1.getName(), hBox);
                accordion.getPanes().add(studyModulePane);
            }
        }

        // Courses

        if (studyModule != null && studyModule.getCourseUnits() != null) {

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
            TitledPane coursesPane = new TitledPane(studyModule.getName(), coursesBox);
            accordion.getPanes().add(coursesPane);
        }

        return accordion;
    }

}