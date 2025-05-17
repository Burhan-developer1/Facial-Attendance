
import java.io.File;
import java.io.FileOutputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bytedeco.flycapture.FlyCapture2_C.syncContext;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import java.nio.IntBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
@SuppressWarnings("unused")

public class AttendanceManagement extends Application {
    public Button clearButton,deleteReport,updateButton;
    
    private static final double MATCH_THRESHOLD = 70.0;
    private static final String DB_URL = "jdbc:mysql://localhost:3307/attendance_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Pakistan@+900";
    private static Map<Integer, String> studentDatabase = new HashMap<>();
    private static BorderPane contentFrame;
    

    
    static {
        System.load("C:\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }
      String imagePath = " ";
    private Connection conn;
    ImageView imageView = new ImageView();
    @Override
    public void start(Stage stage)
    {
        
        // Header Frame
        Label topLabel = new Label("Facial Attendance Management System");
        topLabel.setStyle("-fx-font-size:35px;-fx-font-weight:Bold");
        StackPane topFrame = new StackPane(topLabel);
        topFrame.setStyle("-fx-Background-color:rgb(67, 164, 190)");


        // Side Bar Frame
        VBox sideFrame = new VBox();
        sideFrame.setPrefWidth(250);
        sideFrame.setStyle("-fx-Background-color:rgba(0, 0, 0, 0.99)");
        Image logo = new Image(getClass().getResource("/logos.png").toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(200);
        logoView.setFitWidth(250);
        sideFrame.getChildren().add(logoView);
        sideFrame.setAlignment(Pos.TOP_CENTER);

        Button manageDetails = new Button("Manage Details");
        manageDetails.setStyle("-fx-font-size:20;-fx-font-weight:Bold;-fx-Background-color:rgb(84,188,240);-fx-Background-radius:20");
        manageDetails.setPrefHeight(30);
        manageDetails.setPrefWidth(240);
        Button trainSystem = new Button("Train System");
        trainSystem.setStyle("-fx-font-size:20;-fx-font-weight:Bold;-fx-Background-color:rgb(84,188,240);-fx-Background-radius:20");
        trainSystem.setPrefHeight(30);
        trainSystem.setPrefWidth(240);
        Button viewReport = new Button("View Report");
        viewReport.setStyle("-fx-font-size:20;-fx-font-weight:Bold;-fx-Background-color:rgb(84,188,240);-fx-Background-radius:20");
        viewReport.setPrefHeight(30);
        viewReport.setPrefWidth(240);
        Button changePassword = new Button("Change Password");
        changePassword.setStyle("-fx-font-size:20;-fx-font-weight:Bold;-fx-Background-color:rgb(84,188,240);-fx-Background-radius:20");
        changePassword.setPrefHeight(30);
        changePassword.setPrefWidth(240);
        Button exit = new Button("Start Attendance");
        exit.setStyle("-fx-font-size:20;-fx-font-weight:Bold;-fx-Background-color:rgb(84,188,240);-fx-Background-radius:20");
        exit.setPrefHeight(30);
        exit.setPrefWidth(240);





        GridPane pane = new GridPane();
        pane.setVgap(15);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.add(manageDetails, 1, 1);
        pane.add(trainSystem, 1, 2);
        pane.add(viewReport, 1, 3);
        pane.add(changePassword, 1, 4);
        pane.add(exit, 1, 5);
  
        sideFrame.getChildren().add(pane);

        // Content Frame
        contentFrame = new BorderPane();
        contentFrame.setPrefHeight(550);
        contentFrame.setPrefWidth(950);
        contentFrame.setStyle("-fx-Background-color:rgb(191, 198, 202)");
        Image image = new Image("Background.png");
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(contentFrame.widthProperty());
        imageView.fitHeightProperty().bind(contentFrame.heightProperty());
        imageView.setEffect(new GaussianBlur(5));
        Button button1 = createButton("StudentDetails.png","Student Details");

        Button button2 = createButton("TrainModel.png","Train Model");

        Button button3 = createButton("Report.png","View Report");

        Button button4 = createButton("ChangePassword.png","Change Password");

        Button button5 = createButton("Exit.png","Start Attendance");


        HBox hBox1 = new HBox(30);
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(button1,button2,button3);
        HBox hBox2 = new HBox(30);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(button4,button5);
        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBox1,hBox2);
        contentFrame.getChildren().add(imageView);
        contentFrame.setCenter(vBox);


        // Button Action Calls
        manageDetails.setOnAction(e->{
        contentFrame.getChildren().clear();
        contentFrame.setCenter(manageDetails(stage));
        }
        );
  trainSystem.setOnAction(e -> {
    // Clear existing components from contentFrame
    contentFrame.getChildren().clear();
    contentFrame.setStyle("-fx-background-color:rgb(16, 145, 219) ;");
    
    // Create a VBox for layout
    VBox vbox = new VBox(10);
    vbox.setAlignment(Pos.CENTER);

    // Load and display an image
    Image images = new Image("TrainModel.png");
    ImageView imageViews = new ImageView(images);
    imageViews.setFitWidth(500);
    imageViews.setPreserveRatio(true);

    // Create labels
    Label titleLabel = new Label("Face Training System");
    titleLabel.setStyle("-fx-font-size: 37px; -fx-font-weight: bold;-fx-text-fill: rgb(217, 231, 225)");

    Label statusLabel = new Label("Status: Waiting to Start");
    statusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;-fx-text-fill: rgb(116, 223, 173)");

    // Create a progress bar
    ProgressBar progressBar = new ProgressBar(0);
    progressBar.setPrefHeight(15);
    progressBar.setPrefWidth(350);

    // Create buttons
    Button startButton = new Button("Start Training");
    startButton.setStyle("-fx-background-color: Blue; -fx-font-weight: bold;-fx-text-fill: white;");
    startButton.setPrefHeight(50);
    startButton.setPrefWidth(150);
    
    Button cancelButton = new Button("Cancel");
    cancelButton.setStyle("-fx-background-color: Red; -fx-font-weight: bold;-fx-text-fill: white;");
    cancelButton.setPrefHeight(50);
    cancelButton.setPrefWidth(150);

    // Add elements to the VBox
    vbox.getChildren().addAll(titleLabel, imageViews, statusLabel, progressBar, startButton, cancelButton);
    vbox.setAlignment(Pos.CENTER);
        

    // Set the VBox on contentFrame
    contentFrame.setCenter(vbox);    // contentFrame.setAlignment(Pos.TOP_CENTER);

    // Set button actions
    startButton.setOnAction(event -> {
        statusLabel.setText("Status: Training in Progress...");
        startButton.setDisable(true);
        cancelButton.setDisable(false);
        
        // Create a background task for training
        Task<Void> trainingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Initialize progress
                    updateProgress(0, 100);
                    updateMessage("Connecting to database...");
                    
                    // Get database connection
                    try (Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3307/attendance_db", 
                            "root", 
                            "Pakistan@+900")) {
                        
                        updateMessage("Loading student images...");
                        updateProgress(10, 100);
                        
                        // Get student images
                        Map<Integer, List<String>> studentImages = NewFaceTrainer.getStudentImagePaths(conn);
                        
                        if (studentImages.isEmpty()) {
                            updateMessage("No new students to train");
                            updateProgress(100, 100);
                            return null;
                        }
                        
                        updateMessage("Processing " + studentImages.size() + " students...");
                        updateProgress(20, 100);
                        
                        // Train the model
                        NewFaceTrainer.trainModel(studentImages, conn);
                        
                        updateMessage("Training completed successfully!");
                        updateProgress(100, 100);
                        
                    } catch (SQLException ex) {
                        updateMessage("Database error: " + ex.getMessage());
                        throw ex;
                    }
                } catch (Exception ex) {
                    updateMessage("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
                return null;
            }
        };
        
        // Bind UI updates to task properties
        statusLabel.textProperty().bind(trainingTask.messageProperty());
        progressBar.progressProperty().bind(trainingTask.progressProperty());
        
        // Handle task completion
        trainingTask.setOnSucceeded(ev -> {
            startButton.setDisable(false);
            cancelButton.setDisable(true);
            
            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Training Complete");
            alert.setHeaderText("Face Training Finished");
            alert.setContentText("The face recognition model has been successfully trained!");
            alert.showAndWait();
        });
        
        trainingTask.setOnFailed(ev -> {
            startButton.setDisable(false);
            cancelButton.setDisable(true);
            
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Training Failed");
            alert.setHeaderText("Error During Training");
            alert.setContentText(trainingTask.getException().getMessage());
            alert.showAndWait();
        });
        
        // Start the task in a new thread
        new Thread(trainingTask).start();
    });

    cancelButton.setOnAction(event -> {
        statusLabel.setText("Status: Training Cancelled");
        progressBar.setProgress(0);
        startButton.setDisable(false);
        cancelButton.setDisable(true);
        
        // Note: This won't actually stop the running task - you'd need to implement
        // proper task cancellation for that
    });
});


        
        viewReport.setOnAction(e->{
        contentFrame.getChildren().clear();
        try {
            contentFrame.setCenter(viewReport());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
        );
        changePassword.setOnAction(e->{
        contentFrame.getChildren().clear();
            contentFrame.setStyle("-fx-background-color:rgb(188, 224, 245) ;");

        contentFrame.setCenter(changePassword());
        }
        );
exit.setOnAction(e -> {
    // Close the current window
    Stage currentStage = (Stage) exit.getScene().getWindow();

    // Launch face recognition in a background thread
    new Thread(() -> {
        try {
            // Initialize OpenCV in the same thread
            Loader.load(opencv_face.class);
            
            // Start recognition
            NewFaceRecognizer recognizer = new NewFaceRecognizer();
            recognizer.startRecognition();
        } catch (Exception ex) {
            // Show error in JavaFX thread
            Platform.runLater(() -> 
                new Alert(Alert.AlertType.ERROR, "Failed to start: " + ex.getMessage()).show());
        }
    }).start();
});





        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topFrame);
        borderPane.setLeft(sideFrame);
        borderPane.setCenter(contentFrame);
        Scene scene = new Scene(borderPane,1280,650);
        stage.setScene(scene);
        stage.show();


    }
    public BorderPane manageDetails(Stage stage)
    {
        connectToDatabase();
        BorderPane mainWindow = new BorderPane();
        mainWindow.setStyle("-fx-background-color: white;");

        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #C8102E; -fx-padding: 15; -fx-border-radius: 5px;");
        leftPanel.setPrefWidth(300);
        String setStylee = "-fx-font-size:15;-fx-pref-height:30px;-fx-text-fill: black;-fx-background-color:rgb(241, 244, 247);" +
        "-fx-border-color:rgb(119, 181, 247);";

        Label manageEmployeeLabel = new Label("Manage Students");
        manageEmployeeLabel.setStyle("-fx-font-size:18px;-fx-pref-width:240px;-fx-pref-height:40px;-fx-font-weight:bold; -fx-text-fill: white;");

  ComboBox<String> departmentBox = new ComboBox<>();
departmentBox.getItems().addAll("CIT", "CV", "IT", "ET");
departmentBox.setPromptText("Select Department");

// Set both JavaFX properties and CSS
departmentBox.setPrefWidth(300);
departmentBox.setPrefHeight(10);
departmentBox.setStyle(
    "-fx-font-size: 15px;" +
    "-fx-text-fill: white;" +
    "-fx-background-color:rgb(241, 244, 247);" +
    "-fx-border-color:rgb(119, 181, 247);"
);

        TextField empIdField = new TextField();
        empIdField.setPromptText("Student ID");
        empIdField.setStyle(setStylee);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle(setStylee);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle(setStylee);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle(setStylee);

        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("D.O.B (dd-mm-yyyy)");
        dobPicker.setPrefWidth(300);

        dobPicker.setStyle(setStylee);


    

        DatePicker dojPicker = new DatePicker();
        dojPicker.setPromptText("D.O.J (dd-mm-yyyy)");
        dojPicker.setPrefWidth(300);

        dojPicker.setStyle(setStylee);

        ComboBox<String> proofBox = new ComboBox<>();
        proofBox.getItems().addAll("CNIC", "Smart Card", "B-Form");
        proofBox.setPromptText("Proof Type");
        proofBox.setPrefWidth(300);

        proofBox.setStyle(setStylee);


        TextField proofNumberField = new TextField();
        proofNumberField.setPromptText("Proof Number");
        proofNumberField.setStyle(setStylee);

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setStyle(setStylee);

        HBox buttonRow2 = new HBox(10);
        Button addButton = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");
        addButton.setStyle("-fx-background-color: green; -fx-font-weight: bold;-fx-text-fill: white;");
        updateBtn.setStyle("-fx-background-color: green; -fx-font-weight: bold;-fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: green; -fx-font-weight: bold;-fx-text-fill: white;");
        clearBtn.setStyle("-fx-background-color: green; -fx-font-weight: bold;-fx-text-fill: white;");
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: Blue; -fx-font-weight: bold;-fx-text-fill: white;");
        saveBtn.setPrefHeight(40);
        saveBtn.setPrefWidth(100);
        buttonRow2.setAlignment(Pos.CENTER);



        buttonRow2.getChildren().addAll(updateBtn, deleteBtn, clearBtn);

        HBox buttonRow1 = new HBox(10);
        Button addPhotoBtn = new Button("PATH");
        TextField updatePhotoBtn = new TextField("Image Path");
        addPhotoBtn.setStyle("-fx-background-color: yellow; -fx-font-weight: bold;");
        updatePhotoBtn.setStyle(setStylee);
        updatePhotoBtn.setPromptText("Image path");
        updatePhotoBtn.setPrefWidth(210);
        addPhotoBtn.setPrefHeight(32);
        updatePhotoBtn.setPrefHeight(30);

        buttonRow1.getChildren().addAll(addPhotoBtn, updatePhotoBtn);
addPhotoBtn.setOnAction(event -> {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select Folder Containing Student Photos");
    File selectedDirectory = directoryChooser.showDialog(stage);
    
    if (selectedDirectory != null) {
        String folderPath = selectedDirectory.getAbsolutePath();
        
        // Show message box with the selected folder path
        String content = "Folder \""+folderPath+"\" Selected Successfully";
        showAlert("Folder Selected", content, Alert.AlertType.INFORMATION);
        updatePhotoBtn.setText(folderPath);

        
    }
});

saveBtn.setOnAction(e -> {
    // Validate required fields
    if (empIdField.getText().isEmpty() || 
        nameField.getText().isEmpty() ||
        !isInteger(empIdField) ||
        updatePhotoBtn.getText().isEmpty()) {
        showAlert("Error", "Student ID(Integer), Name, and Image are required!", Alert.AlertType.ERROR);
        return;
    }

    // Prepare save confirmation
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Save");
    confirmation.setHeaderText("Save Student Record");
    confirmation.setContentText("Are you sure you want to save this student information?");
    
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        saveStudent(
            empIdField.getText(),
            nameField.getText(),
            emailField.getText(),
            phoneField.getText(),
            dobPicker.getValue() != null ? dobPicker.getValue().toString() : "",
            dojPicker.getValue() != null ? dojPicker.getValue().toString() : "",
            departmentBox.getValue(),
            proofBox.getValue(),
            proofNumberField.getText(),
            addressField.getText(),
            updatePhotoBtn.getText()
        );
        showAlert("Success", "Student saved successfully!", Alert.AlertType.INFORMATION);
    }
});
clearBtn.setOnAction(e -> {
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Clear");
    confirmation.setHeaderText("Clear Form");
    confirmation.setContentText("Are you sure you want to clear all fields?");
    
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        empIdField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        dobPicker.setValue(null);
        dojPicker.setValue(null);
        departmentBox.setValue(null);
        proofBox.setValue(null);
        proofNumberField.clear();
        addressField.clear();
        updatePhotoBtn.clear();
        imageView.setImage(null);
        imagePath = "";
    }
});
updateBtn.setOnAction(e -> {
    // Validate selection
    if (empIdField.getText().isEmpty()) {
        showAlert("Error", "Please select a student to update", Alert.AlertType.ERROR);
        return;
    }

    // Prepare update confirmation
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Update");
    confirmation.setHeaderText("Update Student Record");
    confirmation.setContentText("Are you sure you want to update this student's information?");
    
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        // Call update method
        updateStudent(
            empIdField.getText(),
            nameField.getText(),
            emailField.getText(),
            phoneField.getText(),
            dobPicker.getValue().toString(),
            dojPicker.getValue().toString(),
            departmentBox.getValue(),
            proofBox.getValue(),
            proofNumberField.getText(),
            addressField.getText(),
            imagePath
        );
        showAlert("Success", "Student updated successfully!", Alert.AlertType.INFORMATION);
    }
});
deleteBtn.setOnAction(e -> {
    // Validate selection
    if (empIdField.getText().isEmpty()) {
        showAlert("Error", "Please select a student to delete", Alert.AlertType.ERROR);
        return;
    }

    // Prepare delete confirmation
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirm Delete");
    confirmation.setHeaderText("Delete Student Record");
    confirmation.setContentText("Are you sure you want to delete this student? This action cannot be undone.");
    
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        // Call delete method
        deleteStudent(empIdField.getText());
        
        showAlert("Success", "Student deleted successfully!", Alert.AlertType.INFORMATION);
    }
});


        leftPanel.getChildren().addAll(
                manageEmployeeLabel, departmentBox, empIdField, nameField, emailField,
                phoneField, dobPicker, dojPicker, proofBox, proofNumberField, addressField,
                buttonRow1, buttonRow2,saveBtn
        );
        leftPanel.setAlignment(Pos.TOP_CENTER);

        VBox searchPanel = new VBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color:rgb(69, 26, 224); -fx-padding: 10;");
        
        Label searchLabel = new Label("Search By");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        ComboBox<String> searchOptions = new ComboBox<>();
        searchOptions.getItems().addAll("Student ID","Student Name","Department", "Proof Number");
        searchOptions.setPromptText("Select Options");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Search Term");

        HBox searchButtons = new HBox(10);
        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");
        searchButtons.getChildren().addAll(searchBtn, showAllBtn);

        searchPanel.getChildren().addAll(searchLabel, searchOptions, searchField, searchButtons);

        TableView<Student> table = new TableView<>();
        table.setPrefHeight(200);

        VBox departmentPanel = new VBox(10);
        departmentPanel.setPadding(new Insets(10));
        departmentPanel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");

        Label departmentLabel = new Label("Department Management");
        departmentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<String> departmentOptions = new ComboBox<>();
        departmentOptions.getItems().addAll("CIT", "ET", "MT", "CV");
        departmentOptions.setPromptText("Select Options");

        TextField departmentNameField = new TextField();
        departmentNameField.setPromptText("Technalogy Name");

        HBox departmentButtons = new HBox(10);
        Button addDeptBtn = new Button("Add");
        Button updateDeptBtn = new Button("Update");
        Button clearDeptBtn = new Button("Clear");
        departmentButtons.getChildren().addAll(addDeptBtn, updateDeptBtn, clearDeptBtn);

        departmentPanel.getChildren().addAll(departmentLabel, departmentOptions, departmentNameField, departmentButtons);

        VBox rightPanel = new VBox(10);
        rightPanel.getChildren().addAll(searchPanel, table, departmentPanel);



        searchBtn.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            String searchType = searchOptions.getValue();
            
            if (searchTerm.isEmpty() || searchType == null) {
                showAlert("Search Error", "Please select a search type and enter a search term", 
                         Alert.AlertType.WARNING);
                return;
            }
            
            try {
                String sql = "SELECT * FROM students WHERE ";
                
                switch(searchType) {
                    case "Student ID":
                        sql += "stu_id LIKE ?";
                        break;
                    case "Student Name":
                        sql += "name LIKE ?";
                        break;
                    case "Department":
                        sql += "department LIKE ?";
                        break;
                    case "Proof Number":
                        sql += "proof_number LIKE ?";
                        break;
                }
                
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + searchTerm + "%");
                
                ResultSet rs = pstmt.executeQuery();
                AttendanceManagement.populateTableWithResults(table, rs);
                
            } catch (SQLException ex) {
                showAlert("Database Error", "Error executing search: " + ex.getMessage(), 
                         Alert.AlertType.ERROR);
            }
        });

        showAllBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM students";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                populateTableWithResults(table, rs);
            } catch (SQLException ex) {
                showAlert("Database Error", "Error loading all students: " + ex.getMessage(), 
                Alert.AlertType.ERROR);
            }
        });
        // Add Department Button
addDeptBtn.setOnAction(e -> {
    String deptName = departmentNameField.getText().trim();
    String selectedDept = departmentOptions.getValue();
    
    if (deptName.isEmpty()) {
        showAlert("Input Error", "Please enter a department name", Alert.AlertType.WARNING);
        return;
    }
    
    try {
        String sql = "INSERT INTO departments (dept_code, dept_name) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, selectedDept);
        pstmt.setString(2, deptName);
        pstmt.executeUpdate();
        
        showAlert("Success", "Department added successfully", Alert.AlertType.INFORMATION);
        departmentNameField.clear();
    } catch (SQLException ex) {
        showAlert("Database Error", "Error adding department: " + ex.getMessage(), 
                 Alert.AlertType.ERROR);
    }
});

// Update Department Button
updateDeptBtn.setOnAction(e -> {
    String selectedDept = departmentOptions.getValue();
    String newName = departmentNameField.getText().trim();
    
    if (selectedDept == null || newName.isEmpty()) {
        showAlert("Input Error", "Please select a department and enter a new name", 
                 Alert.AlertType.WARNING);
        return;
    }
    
    try {
        String sql = "UPDATE departments SET dept_name = ? WHERE dept_code = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setString(2, selectedDept);
        int affectedRows = pstmt.executeUpdate();
        
        if (affectedRows > 0) {
            showAlert("Success", "Department updated successfully", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "No department found with that code", Alert.AlertType.WARNING);
        }
    } catch (SQLException ex) {
        showAlert("Database Error", "Error updating department: " + ex.getMessage(), 
                 Alert.AlertType.ERROR);
    }
});

// Clear Department Button
clearDeptBtn.setOnAction(e -> {
    departmentOptions.setValue(null);
    departmentNameField.clear();
});

// Load department options when the panel is initialized

// Add selection listener to table
table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    if (newSelection != null) {
        // Populate form fields with selected student's data
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        empIdField.setText(selectedStudent.getStuId());
        nameField.setText(selectedStudent.getName());
        emailField.setText(selectedStudent.getEmail());
        phoneField.setText(selectedStudent.getPhone());
        
        // Set dates (with null checks)
        if (selectedStudent.getDob() != null && !selectedStudent.getDob().isEmpty()) {
            dobPicker.setValue(LocalDate.parse(selectedStudent.getDob()));
        }
        if (selectedStudent.getDoj() != null && !selectedStudent.getDoj().isEmpty()) {
            dojPicker.setValue(LocalDate.parse(selectedStudent.getDoj()));
        }
        
        departmentBox.setValue(selectedStudent.getDepartment());
        proofBox.setValue(selectedStudent.getProofType());
        proofNumberField.setText(selectedStudent.getProofNumber());
        addressField.setText(selectedStudent.getAddress());
        updatePhotoBtn.setText(selectedStudent.getImagePath());
        imagePath = selectedStudent.getImagePath();
    }
});
    

        mainWindow.setLeft(leftPanel);
        mainWindow.setCenter(rightPanel);
        return mainWindow;
        

        
    }


    public static ArrayList<String> getStudentImagePaths(Connection conn) throws SQLException {
        ArrayList<String> paths = new ArrayList<>();
        String sql = "SELECT image_path FROM students";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paths.add(rs.getString("image_path"));
            }
        }
        return paths;
    }

    public static StackPane trainModel(ArrayList<String> paths) {
        StackPane borderPane = new StackPane();
        borderPane = FaceTrainingSystem.trainModel(paths);
        return borderPane;
    }



    
    

    
        public BorderPane viewReport() throws Exception {
            conn = connects(); // Initialize connection once
            BorderPane root = new BorderPane();
            root.setPrefSize(950, 550);
            root.setStyle("-fx-background-color:rgb(217, 217, 217);");
            VBox leftPanel = new VBox(10);
            leftPanel.setPadding(new Insets(10));
            leftPanel.setStyle("-fx-background-color: #165B8A; -fx-padding: 15;");
            leftPanel.setPrefWidth(250);
    
            Label updateLabel = new Label("Update Attendance");
            updateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
    
            TextField empID = new TextField();
            empID.setPromptText("Student ID");
    
            TextField name = new TextField();
            name.setPromptText("Name");
    
            DatePicker datePicker = new DatePicker();
            datePicker.setPromptText("Select Date");
    
            ComboBox<String> attendanceStatus = new ComboBox<>();
            attendanceStatus.getItems().addAll("Present", "Absent", "Leave");
            attendanceStatus.setPromptText("Attendance Status");
    
            updateButton = new Button("Update");
            updateButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
    
            clearButton = new Button("Clear");
            clearButton.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold;");
    
            HBox buttons = new HBox(10, updateButton, clearButton);
            buttons.setAlignment(Pos.CENTER);
    
            Label exportLabel = new Label("Export in Excel file");
            exportLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
    
            DatePicker exportDatePicker = new DatePicker();
            exportDatePicker.setPromptText("Select Date");
    
            Button exportCurrentDate = new Button("Export as Current Date");
            exportCurrentDate.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
    
            Button exportEnteredDate = new Button("Export as Entered Date");
            exportEnteredDate.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white;");
    
            deleteReport = new Button("Delete Attendance Report");
            deleteReport.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
    
            leftPanel.getChildren().addAll(
                    updateLabel, empID, name, datePicker, attendanceStatus, buttons,
                    exportLabel, exportDatePicker, exportCurrentDate, exportEnteredDate, deleteReport
            );
    
        
    
            VBox rightPanel = new VBox(10);
            rightPanel.setPadding(new Insets(10));
    
            // Search components
            HBox searchBox = new HBox(10);
            Label searchLabel = new Label("Search By");
            ComboBox<String> searchOptions = new ComboBox<>();
            searchOptions.getItems().addAll("stu_id", "name", "date"); // Match database column names
            searchOptions.setPromptText("Select Options");
    
            TextField searchField = new TextField();
            searchField.setPromptText("Search...");
    
            Button searchButton = new Button("Search");
            Button todayReportButton = new Button("Today's Report");
            Button showAllButton = new Button("Show All");
    
            searchBox.getChildren().addAll(searchLabel, searchOptions, searchField, searchButton, todayReportButton, showAllButton);
    
            // TableView for Attendance Report
            TableView<Attendance> tableView = new TableView<>();
            tableView.setPrefHeight(400);
    
            // Set up columns
            TableColumn<Attendance, String> colID = new TableColumn<>("Attendance ID");
            colID.setCellValueFactory(new PropertyValueFactory<>("attendanceId"));
    
            TableColumn<Attendance, String> colEmpID = new TableColumn<>("Student ID");
            colEmpID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
    
            // TableColumn<Attendance, String> colName = new TableColumn<>("Name");
            // colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    
            TableColumn<Attendance, String> colDate = new TableColumn<>("Date");
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    
            TableColumn<Attendance, String> colStatus = new TableColumn<>("Attendance Status");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    
            TableColumn<Attendance, String> colTime = new TableColumn<>("Time");
            colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    
            tableView.getColumns().addAll(colID, colEmpID, colDate, colStatus, colTime);
    
            // Load initial data
            loadAttendanceData(tableView);
    
            rightPanel.getChildren().addAll(searchBox, tableView);
        
    
            // ================== BUTTON FUNCTIONALITIES ==================
    
            // Update Button - Update attendance record
            updateButton.setOnAction(e -> {
                if (validateUpdateFields(empID, name, datePicker, attendanceStatus)) {
                    updateAttendance(empID.getText(), name.getText(), 
                            datePicker.getValue(), 
                            attendanceStatus.getValue(), tableView);
                }
            });
    
            // Clear Button - Clear input fields
            clearButton.setOnAction(e -> {
                empID.clear();
                name.clear();
                datePicker.setValue(null);
                attendanceStatus.setValue(null);
            });
    
            // Delete Report Button
            deleteReport.setOnAction(e -> {
                if (exportDatePicker.getValue() != null) {
                    deleteAttendanceReport(exportDatePicker.getValue(), tableView);
                } else {
                    showAlert("Error", "Please select a date to delete");
                }
            });
    
            // Search Button
            searchButton.setOnAction(e -> {
                if (searchOptions.getValue() != null && !searchField.getText().isEmpty()) {
                    searchAttendance(searchOptions.getValue(), searchField.getText(), tableView);
                } else {
                    showAlert("Error", "Please select search option and enter search term");
                }
            });
    
            // Today's Report Button
            todayReportButton.setOnAction(e -> {
                showTodaysReport(tableView);
            });
    
            // Show All Button
            showAllButton.setOnAction(e -> {
                loadAttendanceData(tableView);
            });
            root.setLeft(leftPanel);
            root.setCenter(rightPanel);
            return root;

    
        }
    
        private boolean validateUpdateFields(TextField empID, TextField name, 
                                          DatePicker datePicker, ComboBox<String> attendanceStatus) {
            if (empID.getText().isEmpty() || name.getText().isEmpty() || 
                datePicker.getValue() == null || attendanceStatus.getValue() == null) {
                showAlert("Validation Error", "All fields are required");
                return false;
            }
            
            if (!empID.getText().matches("\\d+")) {
                showAlert("Validation Error", "Student ID must be numeric");
                return false;
            }
            
            return true;
        }
    
        private void updateAttendance(String studentId, String name, LocalDate date, 
                                          String status, TableView<Attendance> tableView) {
            try {
                // Check if record exists first
                String checkQuery = "SELECT * FROM attendance WHERE stu_id = ? AND date = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkQuery);
                checkPs.setString(1, studentId);
                checkPs.setDate(2, Date.valueOf(date));
                ResultSet rs = checkPs.executeQuery();
                
                if (rs.next()) {
                    // Update existing record
                    String updateQuery = "UPDATE attendance SET status = ?, name = ? WHERE stu_id = ? AND date = ?";
                    PreparedStatement ps = conn.prepareStatement(updateQuery);
                    ps.setString(1, status);
                    ps.setString(2, name);
                    ps.setString(3, studentId);
                    ps.setDate(4, Date.valueOf(date));
                    ps.executeUpdate();
                } else {
                    // Insert new record
                    String insertQuery = "INSERT INTO attendance (stu_id, name, date, status, check_in_time) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(insertQuery);
                    ps.setString(1, studentId);
                    ps.setString(2, name);
                    ps.setDate(3, Date.valueOf(date));
                    ps.setString(4, status);
                    ps.setTime(5, Time.valueOf(java.time.LocalTime.now()));
                    ps.executeUpdate();
                }
                
                // Refresh table
                loadAttendanceData(tableView);
                showAlert("Success", "Attendance record updated successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to update attendance: " + e.getMessage());
                e.printStackTrace();
            }
        }
    
        private void deleteAttendanceReport(LocalDate date, TableView<Attendance> tableView) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Delete Attendance Report");
            confirmation.setContentText("Are you sure you want to delete all records for " + date + "?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String query = "DELETE FROM attendance WHERE date = ?";
                        PreparedStatement ps = conn.prepareStatement(query);
                        ps.setDate(1, Date.valueOf(date));
                        int affectedRows = ps.executeUpdate();
                        
                        // Refresh table
                        loadAttendanceData(tableView);
                        showAlert("Success", "Deleted " + affectedRows + " attendance records");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to delete records: " + e.getMessage());
                    }
                }
            });
        }
    
        private void searchAttendance(String searchOption, String searchTerm, 
                                    TableView<Attendance> tableView) {
            try {
                ObservableList<Attendance> filteredData = FXCollections.observableArrayList();
                
                String query = "SELECT * FROM attendance WHERE " + searchOption + " LIKE ?";
                PreparedStatement ps = conn.prepareStatement(query);
                
                // Handle date search differently
                if (searchOption.equals("date")) {
                    try {
                        LocalDate searchDate = LocalDate.parse(searchTerm);
                        ps.setDate(1, Date.valueOf(searchDate));
                    } catch (Exception e) {
                        showAlert("Error", "Invalid date format. Please use YYYY-MM-DD");
                        return;
                    }
                } else {
                    ps.setString(1, "%" + searchTerm + "%");
                }
                
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    filteredData.add(new Attendance(
                        rs.getString("id"),
                        rs.getString("stu_id"),
                        rs.getString("date"),
                        rs.getString("status"),
                        rs.getString("check_in_time")
                    ));
                }
                
                tableView.setItems(filteredData);
                
                if (filteredData.isEmpty()) {
                    showAlert("Info", "No records found matching your search");
                }
            } catch (Exception e) {
                showAlert("Error", "Search failed: " + e.getMessage());
            }
        }
    
        private void showTodaysReport(TableView<Attendance> tableView) {
            try {
                ObservableList<Attendance> todaysData = FXCollections.observableArrayList();
                
                String query = "SELECT * FROM attendance WHERE date = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    todaysData.add(new Attendance(
                        rs.getString("id"),
                        rs.getString("stu_id"),
                        rs.getString("date"),
                        rs.getString("status"),
                        rs.getString("check_in_time")
                    ));
                }
                
                tableView.setItems(todaysData);
                
                if (todaysData.isEmpty()) {
                    showAlert("Info", "No attendance records for today");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to load today's report: " + e.getMessage());
            }
        }
    
        private void loadAttendanceData(TableView<Attendance> tableView) {
            try {
                ObservableList<Attendance> data = FXCollections.observableArrayList();
                
                String query = "SELECT * FROM attendance ORDER BY date DESC, check_in_time DESC";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                
                while (rs.next()) {
                    data.add(new Attendance(
                        rs.getString("id"),
                        rs.getString("stu_id"),
                        rs.getString("date"),
                        rs.getString("status"),
                        rs.getString("check_in_time")
                    ));
                }
                
                tableView.setItems(data);
            } catch (Exception e) {
                showAlert("Error", "Failed to load attendance data: " + e.getMessage());
            }
        }
    
        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    
        private Connection connects() throws SQLException {
            // Replace with your actual database connection details
            String url = "jdbc:mysql://localhost:3307/attendance_db";
            String user = "root";
            String password = "Pakistan@+900";
            return DriverManager.getConnection(url, user, password);
        }
    
    // -------------------------------------------------------------------------
    
    public BorderPane changePassword() {
        // Header
        Label addBookLabel = new Label("Change Password");
        StackPane addBookPane = new StackPane(addBookLabel);
        addBookPane.setStyle("-fx-Background-color:rgb(111, 102, 245)");
        addBookLabel.setAlignment(Pos.CENTER);
        addBookLabel.setPrefHeight(50);
        addBookLabel.setPrefWidth(350);
        addBookLabel.setStyle("-fx-font-size:35px;-fx-font-weight:Bold");
    
        // Form fields
        GridPane pane = new GridPane();
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPrefHeight(50);
        oldPasswordField.setPrefWidth(300);
        oldPasswordField.setStyle("-fx-font-size:20px;-fx-font-weight:Bold;-fx-corner-radius:20;-fx-Background-radius:25");
        oldPasswordField.setPromptText("Old Password");
    
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPrefHeight(50);
        newPasswordField.setPrefWidth(300);
        newPasswordField.setStyle("-fx-font-size:20px;-fx-font-weight:Bold;-fx-Background-radius:25");
        newPasswordField.setPromptText("New Password");
    
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPrefHeight(50);
        confirmPasswordField.setPrefWidth(300);
        confirmPasswordField.setStyle("-fx-font-size:20px;-fx-font-weight:Bold;-fx-Background-radius:25");
        confirmPasswordField.setPromptText("Confirm Password");
    
        Button changeButton = new Button("Change Password");
        changeButton.setPrefHeight(50);
        changeButton.setPrefWidth(300);
        changeButton.setStyle("-fx-font-size:25px;-fx-font-weight:Bold;-fx-Background-radius:25;-fx-Background-color:rgb(37, 140, 209)");
    
        // Add components to grid
        pane.add(oldPasswordField, 1, 1);
        pane.add(newPasswordField, 1, 2);  
        pane.add(confirmPasswordField, 1, 3); 
        pane.add(changeButton, 1, 4);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setVgap(15);
    
        // Main container
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(addBookPane);
        mainPane.setCenter(pane);
    
        // Button functionality
        changeButton.setOnAction(e -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
    
            // Validation
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "All fields are required", Alert.AlertType.ERROR);
                return;
            }
    
            if (!newPassword.equals(confirmPassword)) {
                showAlert("Error", "New passwords don't match", Alert.AlertType.ERROR);
                return;
            }
    
            if (newPassword.length() < 8) {
                showAlert("Error", "Password must be at least 8 characters", Alert.AlertType.ERROR);
                return;
            }
    
            // Verify old password and update
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                // Verify old password
                String verifySql = "SELECT * FROM admin WHERE username = ? AND password = ?";
                PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
                verifyStmt.setString(1, "admin"); // Assuming single admin user
                verifyStmt.setString(2, oldPassword);
                
                ResultSet rs = verifyStmt.executeQuery();
                
                if (!rs.next()) {
                    showAlert("Error", "Incorrect old password", Alert.AlertType.ERROR);
                    return;
                }
    
                // Update password
                String updateSql = "UPDATE admin SET password = ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, "admin");
                
                int rowsUpdated = updateStmt.executeUpdate();
                
                if (rowsUpdated > 0) {
                    showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
                    oldPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                } else {
                    showAlert("Error", "Failed to change password", Alert.AlertType.ERROR);
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "Error: " + ex.getMessage(), Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });
    
        return mainPane;
    }
    
 

    // Content Frame Buttons 
    public Button createButton(String path,String name)
    {
    Image image = new Image(path);
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(225);
    imageView.setFitHeight(180);

    Label text = new Label(name);

    
    text.setStyle("-fx-padding: 5px 10px; -fx-text-fill: white;-fx-background-color:rgb(33, 81, 153);-fx-font-weight: bold;-fx-font-size: 18px;");
    


    VBox box = new VBox();
    box.setAlignment(Pos.CENTER);

    box.getChildren().addAll(imageView,text);
    Button button = new Button();
    button.setStyle("-fx-background-color:rgb(33, 81, 153);-fx-background-radius: 15px;");
    button.setGraphic(box);
    return button;


    }
 
 public void saveStudent(String stuId, String name, String email, String phone, String dob, String doj, String department, String proofType, String proofNumber, String address, String imagePath)
    {
        String sql = "INSERT INTO students (stu_id, name, email, phone, dob, doj, department, proof_type, proof_number, address, image_path)Values(?,?,?,?,?,?,?,?,?,?,?)";
    
    try(PreparedStatement pstmt = conn.prepareStatement(sql)){
        pstmt.setString(1, stuId);
        pstmt.setString(2, name);
        pstmt.setString(3, email);
        pstmt.setString(4, phone);
        pstmt.setString(5, dob);
        pstmt.setString(6, doj);
        pstmt.setString(7, department);
        pstmt.setString(8, proofType);
        pstmt.setString(9, proofNumber);
        pstmt.setString(10, address);
        pstmt.setString(11, imagePath);
    
        int rowsInserted = pstmt.executeUpdate();
        if(rowsInserted>0)
        {
            showAlert("Success", "Student data saved successfully!", Alert.AlertType.INFORMATION);

        }
    }
        catch(Exception e){
            e.printStackTrace();
            showAlert("Database Error", "Failed to insert data into MySQL!", Alert.AlertType.ERROR);

        }
    }
        public void connectToDatabase()
        {
            try
            {
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3307/attendance_db","root","Pakistan@+900");
    }
    catch(SQLException e)
    {
        showAlert("Database Error", "Failed to connect to MySQL!", Alert.AlertType.ERROR);

    }
}
private void updateStudent(String stuId, String name, String email, String phone,
                         String dob, String doj, String department,
                         String proofType, String proofNumber,
                         String address, String imagePath) {
    try {
        String sql = "UPDATE students SET name=?, email=?, phone=?, dob=?, doj=?, " +
                     "department=?, proof_type=?, proof_number=?, address=?, image_path=? " +
                     "WHERE stu_id=?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, dob);
            pstmt.setString(5, doj);
            pstmt.setString(6, department);
            pstmt.setString(7, proofType);
            pstmt.setString(8, proofNumber);
            pstmt.setString(9, address);
            pstmt.setString(10, imagePath);
            pstmt.setString(11, stuId);
            
            pstmt.executeUpdate();
        }
    } catch (SQLException ex) {
        showAlert("Database Error", "Failed to update student: " + ex.getMessage(), Alert.AlertType.ERROR);
    }
}
private void deleteStudent(String stuId) {
    try {
        String sql = "DELETE FROM students WHERE stu_id=?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stuId);
            pstmt.executeUpdate();
        }
    } catch (SQLException ex) {
        showAlert("Database Error", "Failed to delete student: " + ex.getMessage(), Alert.AlertType.ERROR);
    }
}
private void showAlert(String title, String message, Alert.AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}

public static boolean isInteger(TextField textField)
{
        try {
            Integer.parseInt(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
}
private static void populateTableWithResults(TableView<Student> table, ResultSet rs) throws SQLException {
    ObservableList<Student> data = FXCollections.observableArrayList();

    while (rs.next()) {
        data.add(new Student(
            rs.getString("stu_id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getString("proof_number")
        ));
    }

    // Clear existing columns
    table.getColumns().clear();

    // Create and add columns
    TableColumn<Student, String> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("stuId"));

    TableColumn<Student, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<Student, String> deptCol = new TableColumn<>("Department");
    deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

    TableColumn<Student, String> proofCol = new TableColumn<>("Proof Number");
    proofCol.setCellValueFactory(new PropertyValueFactory<>("proofNumber"));

    table.getColumns().addAll(idCol, nameCol, deptCol, proofCol);
    table.setItems(data);
}
public static Connection connect() {
    try {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    } catch (SQLException e) {
        System.out.println("Database Connection Failed: " + e.getMessage());
        return null;
    }
}





// All is well

    public static void main(String[] args) {
        launch(args);
    }

}
