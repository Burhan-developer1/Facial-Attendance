import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.nio.FloatBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class NewFaceRecognizer {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/attendance_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Pakistan@+900";


    private static final String MODEL_FILE = "face_model.yml";
    private static final String CAFFE_MODEL = "res10_300x300_ssd_iter_140000.caffemodel";
    private static final String CAFFE_CONFIG = "deploy.prototxt";
    private static final Scalar MEAN_VALUES = new Scalar(104.0, 177.0, 123.0, 0);
    private static final double CONFIDENCE_THRESHOLD = 0.5;
    private static final int PREDICTION_THRESHOLD = 70; // Lower means more strict
      // Track recognized faces to avoid duplicate entries
      private static int lastRecognizedId = -1;
      private static long lastRecognitionTime = 1;
      private static final long COOLDOWN_PERIOD = 30000; 

    // Replace main() with this:
        private BorderPane recognitionPane;
    private boolean isRunning = false;
    
    public BorderPane startRecognition() {
        recognitionPane = new BorderPane();
        isRunning = true;
        
        // Run recognition in background thread
        new Thread(() -> {
            try {
                Loader.load(opencv_face.class);
                startRecognitions();
            } catch (Exception e) {
                Platform.runLater(() -> 
                    new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show());
            }
        }).start();
        
        return recognitionPane;
    }
    public void startRecognitions() throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        // Load face recognition model
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read(MODEL_FILE);

        // Load face detection model
        Net faceDetector = opencv_dnn.readNetFromCaffe(CAFFE_CONFIG, CAFFE_MODEL);

        // Open webcam
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(600);  // Force higher resolution
        grabber.setImageHeight(400);
        grabber.setFrameRate(30);
        grabber.start();

        // Create frame converter
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();


        // Create window for display
       
        // Create window for display
        CanvasFrame frame = new CanvasFrame("Face Recognition", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setCanvasSize(1040, 580);  // Adjust width and height as needed

// Set window position (e.g., top-left corner)
        frame.setLocation(240, 70);
        
        System.out.println("Starting face recognition. Press 'ESC' to exit...");

        while (frame.isVisible()) {
            // Grab frame from camera
            Frame grabbedFrame = grabber.grab();
            if (grabbedFrame == null) continue;

            // Convert to OpenCV Mat
            Mat img = converter.convert(grabbedFrame);

            // Detect faces
            Mat blob = opencv_dnn.blobFromImage(
                img,
                1.0,
                new Size(300, 300),
                MEAN_VALUES,
                false,
                false,
                opencv_core.CV_32F
            );

            faceDetector.setInput(blob);
            Mat detections = faceDetector.forward();

            // Process detections
            FloatBuffer detectionsBuffer = detections.createBuffer();
            int numDetections = detections.size(2);

            for (int i = 0; i < numDetections; i++) {
                float confidence = detectionsBuffer.get(2 + i * 7);
                if (confidence > CONFIDENCE_THRESHOLD) {
                    int x1 = Math.round(detectionsBuffer.get(3 + i * 7) * img.cols());
                    int y1 = Math.round(detectionsBuffer.get(4 + i * 7) * img.rows());
                    int x2 = Math.round(detectionsBuffer.get(5 + i * 7) * img.cols());
                    int y2 = Math.round(detectionsBuffer.get(6 + i * 7) * img.rows());

                    // Ensure coordinates are within image bounds
                    x1 = Math.max(0, x1);
                    y1 = Math.max(0, y1);
                    x2 = Math.min(img.cols() - 1, x2);
                    y2 = Math.min(img.rows() - 1, y2);

                    Rect faceRect = new Rect(new Point(x1, y1), new Point(x2, y2));
                    Mat face = new Mat(img, faceRect);

                    // Preprocess face for recognition
                    Mat processedFace = new Mat();
                    resize(face, processedFace, new Size(100, 100));
                    cvtColor(processedFace, processedFace, COLOR_BGR2GRAY);
                    equalizeHist(processedFace, processedFace);

                    // Recognize face
                    int[] label = new int[1];
                    double[] confidencee = new double[1];
                    faceRecognizer.predict(processedFace, label, confidencee);

                    // Draw rectangle and label
                    rectangle(img, faceRect, new Scalar(0, 255, 0, 1));
                    String labelText = String.format("Person %d (%.2f)", label[0], confidencee[0]);
                    putText(img, labelText, new Point(x1, y1 - 10), 
                           FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0, 1));

                    // Check if match found (confidence below threshold)
                    if (confidencee[0] < PREDICTION_THRESHOLD) {
                        long currentTime = System.currentTimeMillis();
                        System.out.println("Match found! Person ID: " + label[0] + 
                                          " Confidence: " + confidencee[0]);
                        // return true; // Uncomment if you want to exit on first match
                    
                    if (label[0] != lastRecognizedId || (currentTime - lastRecognitionTime) > COOLDOWN_PERIOD) {
                        System.out.println("Match found! Person ID: " + label[0]);
                        markAttendance(conn, label[0]);
                        lastRecognizedId = label[0];
                        lastRecognitionTime = currentTime;
                }
            }
        }
    }


            // Display the frame
            frame.showImage(grabbedFrame);

            // Clean up
            blob.close();
            detections.close();
        }

        // Cleanup
        frame.dispose();
        grabber.stop();
        faceRecognizer.close();
        conn.close();
    }
    private static void markAttendance(Connection conn, int stu_id) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // First check if attendance already exists for today
        String checkSql = "SELECT id FROM attendance WHERE stu_id = ? AND date = ?";
        String updateSql = "UPDATE attendance SET check_in_time = ? WHERE id = ?";
        String insertSql = "INSERT INTO attendance (stu_id, date, status, check_in_time) VALUES (?, ?, ?, ?)";
        
        try {
            // Check for existing attendance record
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, stu_id);
            checkStmt.setString(2, date);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Existing record found - update check-in time
                int recordId = rs.getInt("id");
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, timestamp);
                updateStmt.setInt(2, recordId);
                
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Updated check-in time for ID: " + stu_id);
                }
                updateStmt.close();
            } else {
                // No record found - insert new attendance
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, stu_id);
                insertStmt.setString(2, date);
                insertStmt.setString(3, "Present");
                insertStmt.setString(4, timestamp);
                
                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("New attendance marked for ID: " + stu_id);
                }
                insertStmt.close();
            }
            
            rs.close();
            checkStmt.close();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    public void stopRecognition() {
        isRunning = false;
        // Add cleanup code
    }
}