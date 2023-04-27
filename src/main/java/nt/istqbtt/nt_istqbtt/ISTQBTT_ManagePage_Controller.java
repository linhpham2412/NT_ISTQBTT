package nt.istqbtt.nt_istqbtt;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.tess4j.TesseractException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import static nt.istqbtt.nt_istqbtt.ConvertImageToText.convertImageFileToText;
import static nt.istqbtt.nt_istqbtt.EncryptDecryptBased64.decryptedBase64TextWithSecretKey;
import static nt.istqbtt.nt_istqbtt.EncryptDecryptBased64.encryptTextBase64WithSecretKey;

public class ISTQBTT_ManagePage_Controller implements Initializable {
    public Pane managePagePane;
    public VBox managePageVBox;
    Stage mainStage;
    Font toolFont = new Font(24);
    double screenWidth = 1500;
    double screenHeight = 850;
    FileChooser fileChooser = new FileChooser();
    String currentPath = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            currentPath = new java.io.File(".").getCanonicalPath();
            managePageVBox = setupManagePage(toolFont);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            throw new RuntimeException(e);
        }
        managePagePane.getChildren().add(managePageVBox);
    }

    private void changeStageAndScene(ActionEvent event, VBox layoutVBoxContainer, String sceneTitle) {
        this.mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Pane layout = new Pane(layoutVBoxContainer);
        Scene scene = new Scene(layout, screenWidth, screenHeight);
        mainStage.setResizable(false);
        mainStage.setTitle(sceneTitle);
        mainStage.setScene(scene);
    }

    private void openNewStageAndScene(VBox layoutVBoxContainer, String sceneTitle) {
        Stage newStage = new Stage();
        Pane layout = new Pane(layoutVBoxContainer);
        Scene scene = new Scene(layout, screenWidth, screenHeight);
        newStage.setResizable(false);
        newStage.setTitle(sceneTitle);
        newStage.setScene(scene);
        newStage.show();
    }

    private VBox setupManagePage(Font toolFont) throws IOException, net.lingala.zip4j.exception.ZipException {
        //Set up layout
        HBox layoutHBox = new HBox();
        VBox imagePaneContainer = new VBox();
        imagePaneContainer.setAlignment(Pos.CENTER);
        imagePaneContainer.setPrefWidth(screenWidth / 2);
        imagePaneContainer.setPrefHeight(screenHeight);
        imagePaneContainer.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        TextArea textToProcessArea = new TextArea();
        textToProcessArea.setWrapText(true);
        textToProcessArea.setPrefSize(screenWidth/2,screenHeight/2.5);
        Button imageSelector = new Button("Open Image");
        imageSelector.setFont(toolFont);
        imageSelector.setOnAction(event -> {
            fileChooser.setInitialDirectory(new File(currentPath));
            File imageFile = fileChooser.showOpenDialog(null);
            ImageView imageToCheck = new ImageView(imageFile.toURI().toString());
            imageToCheck.setFitWidth(screenWidth / 2);
            imageToCheck.setFitHeight(screenHeight / 1.5);
            if (imagePaneContainer.getChildren().size() > 1) {
                imagePaneContainer.getChildren().remove(imagePaneContainer.getChildren().size() - 1);
            }
            imagePaneContainer.getChildren().add(imageToCheck);
            try {
                textToProcessArea.setText(convertImageFileToText(imageFile));
                textToProcessArea.setText(textToProcessArea.getText().replace(" ","")
                        .replace("\n",""));
            } catch (TesseractException e) {
                throw new RuntimeException(e);
            }
        });
        imagePaneContainer.getChildren().add(imageSelector);
        VBox.setMargin(imageSelector, new Insets(10, 10, 10, 10));

        VBox textPaneContainer = new VBox();
        Label textToProcess = new Label("Text To Process:");
        textToProcess.setFont(toolFont);
        HBox textConvertCommandContainer = new HBox();
        Button encryptButton = new Button("Encrypt");
        encryptButton.setFont(toolFont);
        Button decryptButton = new Button("Decrypt");
        decryptButton.setFont(toolFont);
        textConvertCommandContainer.setAlignment(Pos.CENTER);
        textConvertCommandContainer.getChildren().add(encryptButton);
        textConvertCommandContainer.getChildren().add(decryptButton);
        HBox.setMargin(encryptButton,new Insets(5,5,5,5));
        HBox.setMargin(decryptButton,new Insets(5,5,5,5));
        Label textConverted = new Label("Result Text:");
        textConverted.setFont(toolFont);
        TextArea textConvertedArea = new TextArea();
        textConvertedArea.setWrapText(true);
        textConvertedArea.setPrefSize(screenWidth/2,screenHeight/2.5);
        textPaneContainer.getChildren().add(textToProcess);
        textPaneContainer.getChildren().add(textToProcessArea);
        textPaneContainer.getChildren().add(textConvertCommandContainer);
        textPaneContainer.getChildren().add(textConverted);
        textPaneContainer.getChildren().add(textConvertedArea);
        VBox.setMargin(textToProcess,new Insets(5,5,5,5));
        VBox.setMargin(textConverted,new Insets(5,5,5,5));
        VBox.setMargin(textConvertedArea,new Insets(5,5,5,5));
        VBox.setMargin(textToProcessArea,new Insets(5,5,5,5));
        encryptButton.setOnAction(event -> {
            try {
                textConvertedArea.setText(encryptTextBase64WithSecretKey(textToProcessArea.getText(),"123"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        decryptButton.setOnAction(event -> {
            try {
                textConvertedArea.setText(decryptedBase64TextWithSecretKey(textToProcessArea.getText(),"123"));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

        layoutHBox.getChildren().add(imagePaneContainer);
        layoutHBox.getChildren().add(textPaneContainer);

        //Set up Result VBox
        VBox resultVBox = new VBox();
        resultVBox.setPrefWidth(screenWidth);
        resultVBox.setAlignment(Pos.CENTER);
        resultVBox.getChildren().add(layoutHBox);

        return resultVBox;
    }
}