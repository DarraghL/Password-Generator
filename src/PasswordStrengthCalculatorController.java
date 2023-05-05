import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.swing.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;


public class PasswordStrengthCalculatorController {



    @FXML
    private VBox root;
    @FXML
    private Label copiedLabel;
    @FXML
    private Label recommendLabel;
    @FXML
    private Label strengthLabel;
    @FXML
    private TextField passwordField;
    @FXML
    private Button copyButton;
    @FXML
    private Label reasonsLabel;

    private final Random random = new Random();


    public void initialize() {
        copyButton.setOnAction(event -> copyToClipboard());
        passwordField.setOnKeyTyped(event -> calculateStrengthColour());

    }

    @FXML
    private void copyToClipboard() {
        StringSelection selection = new StringSelection(passwordField.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        copiedLabel.setText("Text copied!");
        copiedLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            copiedLabel.setVisible(false);
        }));
        timeline.play();
    }



    @FXML
    private void calculateStrengthColour() {
        List<String> reasons = calculatePasswordStrength(passwordField.getText());
        int strength = reasons.isEmpty() ? 5 : Math.max(0, 5 - reasons.size());

        // Update the strength label
        strengthLabel.getStyleClass().add("strength-label");
        strengthLabel.getStyleClass().setAll("strength-label", "strength-level-" + strength);
        strengthLabel.setText("Password strength: " + strength);

        // Update the recommendation label
        if (strength == 5) {
            recommendLabel.setText("Password is recommended");
            recommendLabel.getStyleClass().setAll("recommend-label");
        } else {
            recommendLabel.setText("Password is not recommended");
            recommendLabel.getStyleClass().setAll("not-recommend-label");
        }

        // Display the reasons for a weak password
        StringBuilder reasonText = new StringBuilder();
        for (String reason : reasons) {
            reasonText.append(reason).append("\n");
        }
        if (!reasons.isEmpty()) {
            if (reasonsLabel == null) {
                reasonsLabel = new Label();
                root.getChildren().add(reasonsLabel); // Add the label to the scene graph
            }
            reasonsLabel.setText(reasonText.toString());
        } else {
            if (reasonsLabel != null) {
                reasonsLabel.setText(reasonText.toString()); // Update the reasons if the label already exists
            } else {
                reasonsLabel = new Label();
                root.getChildren().add(reasonsLabel); // Add the label to the scene graph
                reasonsLabel.setText(reasonText.toString());

            }
        }
    }



    @FXML
    private void generatePassword() {
        String password;
        List<String> reasons;
        int numIterations = 0;

        do {
            // Create a character set including letters, digits, and special characters
            char[] chars = ("abcdefghijklmnopqrstuvwxyz" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()").toCharArray();
            StringBuilder sb = new StringBuilder();

            // Generate a random password of length 20
            for (int i = 0; i < 20; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }

            password = sb.toString();

            // Check the generated password's strength
            reasons = calculatePasswordStrength(password);
            numIterations++;
            // Repeat until the password meets the criteria or the loop has run 1000 times
        } while (reasons.size() < 5 && numIterations < 1000);

        // Update the password field and strength meter
        passwordField.setText(password);
        calculateStrengthColour();
    }



    @FXML
    void imageToPassword() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Open the Windows file explorer to allow the user to select the image file
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
                // Read the contents of the image file into a byte array
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Convert the image to a binary string
                String binaryString = Base64.getEncoder().encodeToString(imageBytes);

                // Decode the Base64 string into binary data
                byte[] binaryData = Base64.getDecoder().decode(binaryString);

                // Convert the binary data to a string using ASCII encoding
                String asciiString = new String(binaryData, StandardCharsets.US_ASCII);

                // Remove any characters that are not in the allowed set
                asciiString = asciiString.replaceAll("[^a-zA-Z0-9!@#$%^&*()]", "");

                // Cluster the ASCII string using k-means clustering
                KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(20, 1000, new EuclideanDistance());
                List<DoublePoint> dataPoints = new ArrayList<DoublePoint>();
                for (int i = 0; i < asciiString.length(); i++) {
                    dataPoints.add(new DoublePoint(new double[]{(double)asciiString.charAt(i)}));
                }
                List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(dataPoints);

                // Build the final string by concatenating the centroids of the clusters
                StringBuilder stringBuilder = new StringBuilder();
                for (CentroidCluster<DoublePoint> cluster : clusters) {
                    stringBuilder.append((char)((int)cluster.getCenter().getPoint()[0]));
                }

                // Set the password field to the generated password
                String generatedPassword = stringBuilder.toString();
                passwordField.setText(generatedPassword);
                calculateStrengthColour();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage());
            }
        }
    }



    List<String> calculatePasswordStrength(String password) {
        List<String> reasons = new ArrayList<>();

        // Check length
        if (password.length() < 20) {
            reasons.add("Password is too short");
        }

        // Check for lowercase letters
        if (!password.matches(".*[a-z].*")) {
            reasons.add("Password does not contain lowercase letters");
        }

        // Check for uppercase letters
        if (!password.matches(".*[A-Z].*")) {
            reasons.add("Password does not contain uppercase letters");
        }

        // Check for digits
        if (!password.matches(".*\\d.*")) {
            reasons.add("Password does not contain digits");
        }

        // Check for special characters
        if (!password.matches(".*[!@#$%^&*()].*")) {
            reasons.add("Password does not contain special characters");
        }

        return reasons;
    }

}