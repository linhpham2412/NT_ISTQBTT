package nt.istqbtt.nt_istqbtt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import nt.istqbtt.nt_istqbtt.datamodel.QuestionDataModel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static nt.istqbtt.nt_istqbtt.EncryptDecryptBased64.encryptTextBase64WithSecretKey;
import static org.apache.commons.io.FileUtils.cleanDirectory;

public class ISTQBTT_Page_Controller implements Initializable {
    public Pane examPagePane;

    public VBox examPageVBox;

    public Button btn_StartTest;
    public ComboBox selectTestingTypeComboBox;
    public TextField testNameTextField;


    Stage mainStage;
    javafx.scene.text.Font toolFont = new javafx.scene.text.Font(24);
    double screenWidth = 1500;
    double screenHeight = 850;
    QuestionHandler questionHandler = new QuestionHandler();
    Pagination pagination;
    String questionFileName = "ISTQB_QuestionsBank";
    String zipFilePassword = "123";
    public static String testUserName;

    //set up testing time
    int testingMinutes = 60;
    Timeline timerTimeLine;

    //set up component size
    double labelWidthInScrollPane = screenWidth * 0.54;
    double checkBoxWidthInScrollPane = screenWidth * 0.25;

    //Question data item
    String[] questionStringTitle = new String[10];
    boolean isQuestionMultipleChoices = false;
    String[] questionStringAnswer = new String[10];
    boolean[] questionBooleanIsAnswerCorrect = new boolean[10];
    int[][] correctAnswer = new int[40][10];

    //Question Field items
    Label[] questionTitle;
    javafx.scene.image.ImageView[] questionImage;
    GridPane[] questionGridTable;
    Object[] questionObjects;
    CheckBox[] answerCheckBoxes;
    RadioButton[] answerRadioButtons;
    ToggleGroup[] answerRadioGroup = new ToggleGroup[40];
    HBox[] answerHBoxContainers;
    Button nextPageButton = new Button("Next");
    Button previousPageButton = new Button("Previous");
    Button endTestButton = new Button("End Test");

    //Answer items
    int[][] selectedAnswer = new int[40][10];


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            examPageVBox = setupHomePage(toolFont);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            throw new RuntimeException(e);
        }
        examPagePane.getChildren().add(examPageVBox);
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

    private VBox setupHomePage(Font toolFont) throws IOException, net.lingala.zip4j.exception.ZipException {
        //Read zip data file
        questionHandler.readQuestionZipFile(questionFileName, zipFilePassword);
        questionHandler.readAndSaveAllISTQBTypeInData(zipFilePassword);
        //Set up layout
        Pane blankPaneHeader = new Pane();
        double headerHeight = screenHeight / 8;
        blankPaneHeader.setPrefHeight(headerHeight);
        blankPaneHeader.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        ImageView nashTechLogo = new ImageView();
        nashTechLogo.setFitWidth(headerHeight);
        nashTechLogo.setFitHeight(headerHeight);
        HBox logoBox = new HBox();
        HBox infoBox = new HBox();
        Button informationButton = new Button("Credit");
        ImageView infoImage = new ImageView(new Image("nt/istqbtt/nt_istqbtt/infomationIcon.png"));
        infoImage.setFitHeight(headerHeight / 2);
        infoImage.setFitWidth(headerHeight / 2);
        informationButton.setGraphic(infoImage);
        informationButton.setOnAction(event -> {
//            changeStageAndScene(event, setupCreditPage(toolFont), "Credit Page");

            try {
                openNewStageAndScene(setupCertificatePage(toolFont), "Certificate Page");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        logoBox.setPrefSize(screenWidth / 2, headerHeight);
        infoBox.setPrefSize(screenWidth / 2, headerHeight);
        logoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setAlignment(Pos.CENTER_RIGHT);
        logoBox.getChildren().add(nashTechLogo);
        infoBox.getChildren().add(informationButton);
        infoBox.setTranslateX(screenWidth / 2);
        HBox.setMargin(informationButton, new Insets(20, 20, 20, 20));
        blankPaneHeader.getChildren().add(logoBox);
        blankPaneHeader.getChildren().add(infoBox);
        nashTechLogo.setImage(new Image("nt/istqbtt/nt_istqbtt/NashTechLogo.png"));
        Pane blankPaneFooter = new Pane();
        blankPaneFooter.setPrefHeight(screenHeight / 8);
        blankPaneFooter.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        Label welcomeTitle = new Label("Welcome To Internal ISTQB Knowledge Testing Tool");
        welcomeTitle.setPrefWidth(screenWidth / 3);
        welcomeTitle.setWrapText(true);
        welcomeTitle.setAlignment(Pos.CENTER);
        welcomeTitle.setStyle("-fx-font-size: 48; -fx-font-weight: bold;-fx-text-alignment: center;");
        HBox welcomeContainer = new HBox();
        HBox welcomeBorder = new HBox();
        Pane blankLeftPane = new Pane();
        blankLeftPane.setPrefWidth(screenWidth / 8);
        Pane blankRightPane = new Pane();
        blankRightPane.setPrefWidth(screenWidth / 8);
        welcomeBorder.setAlignment(Pos.CENTER);
        welcomeBorder.setStyle("-fx-border-color: red; -fx-border-width: 10px; -fx-border-style: solid;");
        welcomeBorder.getChildren().add(welcomeTitle);
        HBox.setMargin(welcomeTitle, new Insets(20, 20, 20, 20));
        welcomeContainer.getChildren().add(blankLeftPane);
        welcomeContainer.getChildren().add(welcomeBorder);
        welcomeContainer.getChildren().add(blankRightPane);
        welcomeContainer.setAlignment(Pos.CENTER);
        HBox.setMargin(welcomeBorder, new Insets(20, 20, 0, 20));
        Label creatorTitle = new Label("By Linh Pham");
        creatorTitle.setFont(toolFont);
        HBox selectTestingTypeHBox = new HBox();
        Label selectYourTestingTypeLabel = new Label("Select ISTQB:");
        selectYourTestingTypeLabel.setFont(toolFont);

        //Set up ISTQB Type information pane
        HBox istqbInformationHBox = new HBox();
        istqbInformationHBox.setPrefSize(screenWidth, screenHeight / 3.5);
        VBox infoVbox = new VBox();
        infoVbox.setPrefWidth(screenWidth / 2);
        infoVbox.setStyle("-fx-font-size: 16; -fx-border-width: 5px; -fx-border-style: solid;-fx-border-color: #3282F6;");
        HBox.setMargin(infoVbox, new Insets(20, 20, 20, 20));
        istqbInformationHBox.setAlignment(Pos.CENTER);
        Label istqbDetailText = new Label("Detail of ISTQB here");
        istqbDetailText.setFont(toolFont);
        istqbDetailText.setStyle("-fx-text-alignment: center;");
        istqbDetailText.setPrefWidth(screenWidth / 2);
        istqbDetailText.setWrapText(true);


        infoVbox.setAlignment(Pos.TOP_LEFT);
        infoVbox.getChildren().add(istqbDetailText);
        infoVbox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setMargin(istqbDetailText, new Insets(5, 0, 0, 5));

        istqbInformationHBox.getChildren().add(infoVbox);
        //End information pane

        selectTestingTypeComboBox = new ComboBox();
        questionHandler.getListOfISTQBTypeReadFromData().stream()
                .map(e -> selectTestingTypeComboBox.getItems().add(e)).collect(Collectors.toList());
        selectTestingTypeComboBox.setStyle("-fx-font-size: 16");
        selectTestingTypeComboBox.setOnAction(event -> {
            questionHandler.questionGroupName = (String) selectTestingTypeComboBox.getValue();
            istqbDetailText.setText(selectTestingTypeComboBox.getValue().toString() + "\nNumber of Question: 40\nPassing Score (at least 65%): 26\nTesting Time: 60 minutes\n" +
                    "Note that the score calculate based on number of correct questions!");
        });
        selectTestingTypeHBox.setAlignment(Pos.CENTER);
        selectTestingTypeHBox.getChildren().add(selectYourTestingTypeLabel);
        selectTestingTypeHBox.getChildren().add(selectTestingTypeComboBox);
        HBox.setMargin(selectYourTestingTypeLabel, new Insets(10, 10, 10, 10));
        HBox.setMargin(selectTestingTypeComboBox, new Insets(10, 10, 10, 10));


        //set up command button
        Label testNameLabel = new Label("Your Name: ");
        testNameLabel.setFont(toolFont);
        testNameTextField = new TextField();
        testNameTextField.setFont(toolFont);
        btn_StartTest = new Button("Start Test");
        btn_StartTest.setFont(toolFont);
        btn_StartTest.setOnAction(event -> {
            if (selectTestingTypeComboBox.getValue() != null) {
                questionHandler.isFirstLoad = true;
                testUserName = testNameTextField.getText();
                try {
                    changeStageAndScene(event, setupLayoutPageExam(toolFont), "Examination Page of: " + selectTestingTypeComboBox.getValue());
                } catch (IOException | net.lingala.zip4j.exception.ZipException zipException) {
                    throw new RuntimeException(zipException);
                }
            }
        });
        Button quitAppHomeButton = new Button("Quit");
        quitAppHomeButton.setFont(toolFont);
        quitAppHomeButton.setOnAction(event -> {
            try {
                cleanDirectory(questionHandler.imagesFolder);
                questionHandler.imagesFolder.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ((Stage) (quitAppHomeButton.getScene().getWindow())).close();
        });
        HBox commandContainer = new HBox();
        commandContainer.setAlignment(Pos.CENTER);
        commandContainer.getChildren().add(testNameLabel);
        commandContainer.getChildren().add(testNameTextField);
        commandContainer.getChildren().add(btn_StartTest);
        commandContainer.getChildren().add(quitAppHomeButton);
        HBox.setMargin(btn_StartTest, new Insets(10, 10, 10, 10));
        HBox.setMargin(quitAppHomeButton, new Insets(10, 10, 10, 10));

        //Set up Result VBox
        VBox resultVBox = new VBox();
        resultVBox.setPrefWidth(screenWidth);
        resultVBox.setAlignment(Pos.CENTER);
        resultVBox.getChildren().add(blankPaneHeader);
        resultVBox.getChildren().add(welcomeContainer);
        resultVBox.getChildren().add(creatorTitle);
        resultVBox.getChildren().add(selectTestingTypeHBox);
        resultVBox.getChildren().add(commandContainer);
        resultVBox.getChildren().add(istqbInformationHBox);
        resultVBox.getChildren().add(blankPaneFooter);

        return resultVBox;
    }

    private VBox setupCertificatePage(Font toolFont) throws Exception {
        Image certificateBackGround = new Image("nt/istqbtt/nt_istqbtt/NTInternalCertificate.png");
        Label testUserNameCertificate = new Label("ANH TRAN THI HUYNH");
        testUserNameCertificate.setStyle("-fx-font-size: 48; -fx-font-weight: bold;-fx-text-alignment: center;");
        testUserNameCertificate.setTextFill(Color.valueOf("#284977"));
        testUserNameCertificate.setFont(new Font("Poppins Extrabold", 36));
        Label testTypeCertificate = new Label("Advanced CTAL-TTA Certified Tester Advanced Level Technical Test Analyst v3.0");
        testTypeCertificate.setStyle("-fx-font-size: 36; -fx-font-weight: bold;-fx-text-alignment: center;");
        testTypeCertificate.setTextFill(Color.valueOf("#FA6070"));
        testTypeCertificate.setFont(new Font("Lato Bold", 36));
        testTypeCertificate.setPrefWidth(screenWidth / 1.8);
        testTypeCertificate.setWrapText(true);
        Label verificationTextLabel = new Label("Verification Text:");
        verificationTextLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        verificationTextLabel.setTextFill(Color.valueOf("#284977"));
        String encryptedText = encryptTextBase64WithSecretKey("This is a certificate to ANH TRAN THI HUYNH | " +
                "Passed ISTQB - Certified Tester Foundation Level | Pass Score 30/40 | Test Date: April 25, 2023", "123");
        System.out.println(encryptedText);
        System.out.println(System.getProperty("java.classpath"));
//        String[] blocks = encryptedText.split("(?<=\\G.{5})");
//        String encryptedLabel = "";
//        for (int i=0;i<blocks.length;i++){
//            encryptedLabel += blocks[i] + " ";
//        }
        Image qrImage = SwingFXUtils.toFXImage(generateQRCodeImage(encryptedText), null);
        ImageView qrcode = new ImageView(qrImage);
        qrcode.setFitHeight(150);
        qrcode.setFitWidth(150);
//        Label encryptedCertificateInfo = new Label(encryptedText);
//        encryptedCertificateInfo.setPrefWidth(screenWidth / 2);
//        encryptedCertificateInfo.setWrapText(true);
//        encryptedCertificateInfo.setStyle("-fx-font-weight: bold;");
////        encryptedCertificateInfo.setTextFill(Color.valueOf("#284977"));
//        encryptedCertificateInfo.setFont(new Font("Poppins Extrabold", 20));
//        System.out.println(encryptedCertificateInfo.getText());
//        System.out.println("Decrypt: " + decryptedBase64TextWithSecretKey(encryptedCertificateInfo.getText(), "123"));

        VBox certificateVbox = new VBox();
        certificateVbox.setAlignment(Pos.TOP_CENTER);
        certificateVbox.setPrefSize(screenWidth, screenHeight);
        certificateVbox.getChildren().add(testTypeCertificate);
        certificateVbox.getChildren().add(testUserNameCertificate);
        certificateVbox.getChildren().add(verificationTextLabel);
//        certificateVbox.getChildren().add(encryptedCertificateInfo);
        certificateVbox.getChildren().add(qrcode);
        VBox.setMargin(testTypeCertificate, new Insets(screenHeight / 3, 0, 0, 0));
        System.out.println(testTypeCertificate.getText().length());
        if (testTypeCertificate.getText().length() < 44) {
            VBox.setMargin(testUserNameCertificate, new Insets(screenHeight / 6.5, 0, 0, 0));
        } else {
            VBox.setMargin(testUserNameCertificate, new Insets(screenHeight / 11, 0, 0, 0));
        }
        VBox.setMargin(verificationTextLabel, new Insets(screenHeight / 6.5, 0, 0, 0));
//        VBox.setMargin(qrcode, new Insets(screenHeight / 8, 0, 0, 0));
        certificateVbox.setBackground(new Background(new BackgroundImage(certificateBackGround, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO,
                BackgroundSize.AUTO, false, false, true, false))));
        return certificateVbox;
    }

    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }



    private VBox setupCreditPage(Font toolFont) {
        HBox creditHeader = new HBox();
        Label thanksLabel = new Label("Thank you to the effort of all members in the team!");
        thanksLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold;-fx-text-alignment: center;");
        thanksLabel.setTextFill(Color.valueOf("#FA6070"));
        thanksLabel.setFont(new Font("Lato Bold", 48));
        Button creditPageReturnHome = new Button("Home");
        creditPageReturnHome.setOnAction(event -> {
            selectedAnswer = new int[40][10];
            questionHandler.isFirstLoad = true;
            try {
                changeStageAndScene(event, setupHomePage(toolFont), "Home Page");
            } catch (IOException | net.lingala.zip4j.exception.ZipException e) {
                throw new RuntimeException(e);
            }
        });

        creditHeader.setAlignment(Pos.CENTER_RIGHT);
        creditHeader.setPrefWidth(screenWidth);
        creditHeader.setPrefHeight(screenHeight / 8);
        creditHeader.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        creditHeader.getChildren().add(thanksLabel);
        creditHeader.getChildren().add(creditPageReturnHome);
        HBox.setMargin(creditPageReturnHome, new Insets(0, 20, 0, 100));

        HBox creditCreator = new HBox();
        creditCreator.setAlignment(Pos.CENTER);
        creditCreator.setPrefWidth(screenWidth);
        creditCreator.setPrefHeight(screenHeight / 8);
        Label creatorName = new Label("Creator: LINH PHAM\n" +
                "linh.pham@nashtechglobal.com");
        creatorName.setStyle("-fx-font-size: 36; -fx-font-weight: bold;-fx-text-alignment: center;");
        creatorName.setTextFill(Color.valueOf("#284977"));
        creatorName.setFont(new Font("Poppins Extrabold", 36));
        creditCreator.getChildren().add(creatorName);

        HBox creditDataCollector = new HBox();
        creditDataCollector.setAlignment(Pos.CENTER);
        creditDataCollector.setPrefWidth(screenWidth);
        Label dataCollectorTitle = new Label("\nData Collector:\n" +
                "ANH TRAN THI HUYNH\nAnh.TranThiHuynh@nashtechglobal.com\n\n" +
                "ANH NGUYEN TA TUYET\nAnh.NguyenTaTuyet@nashtechglobal.com\n\n" +
                "TRAM NGUYEN PHUONG NGUYET\nTram.NguyenPhuongNguyet@nashtechglobal.com");
        dataCollectorTitle.setStyle("-fx-font-size: 36; -fx-font-weight: bold;-fx-text-alignment: center;");
        dataCollectorTitle.setTextFill(Color.valueOf("#284977"));
        dataCollectorTitle.setFont(new Font("Poppins Extrabold", 36));
        creditDataCollector.getChildren().add(dataCollectorTitle);

        VBox creditPageVbox = new VBox();
        creditPageVbox.setAlignment(Pos.CENTER_RIGHT);
        creditPageVbox.getChildren().add(creditHeader);
        creditPageVbox.getChildren().add(creditCreator);
        creditPageVbox.getChildren().add(creditDataCollector);
        return creditPageVbox;
    }

    private VBox setupLayoutPageExam(Font toolFont) throws IOException, net.lingala.zip4j.exception.ZipException {
        //Read and assign all questions data to questionHandler
        questionHandler.readQuestionZipFile(questionFileName, zipFilePassword);
        questionHandler.mapDataInQuestionFileToDataModelByGroupName();
        questionHandler.randomChooseQuestionsInBankThenShuffleAndSaveToTestingQuestions(correctAnswer);

        //Set up Timer header
        Label timerValue = new Label();
        timerValue.setFont(toolFont);
        ProgressBar timerProgressBar = new ProgressBar();
        timerProgressBar.setPrefWidth(screenWidth * 0.87);
        timerProgressBar.setRotate(180);
        timerProgressBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        final int[] seconds = {testingMinutes * 60};
        int totalSeconds = seconds[0];
        timerTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timerValue.setText("Time left " + calculateTimeLeft(seconds));
            seconds[0]--;
            timerProgressBar.setProgress(1 - (double) seconds[0] / totalSeconds);
            if (seconds[0] < 0) timerTimeLine.stop();
        }));
        timerTimeLine.setCycleCount(Animation.INDEFINITE);
        timerTimeLine.play();
        //End of timer
        HBox timerArea = new HBox();
        HBox.setMargin(timerValue, new Insets(5, 5, 5, 5));
        HBox.setMargin(timerProgressBar, new Insets(15, 15, 15, 15));
        timerArea.getChildren().add(timerValue);
        timerArea.getChildren().add(timerProgressBar);

        //Set up Pagination question pages
        pagination = new Pagination(questionHandler.numberOfQuestionsPerQuestionBank);
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pagination.setPageFactory(this::getQuestionPages);
        pagination.setMaxPageIndicatorCount(40);
        pagination.setScaleX(1.7);
        pagination.setScaleY(1.7);
        HBox questionPane = new HBox(pagination);
        questionPane.setAlignment(Pos.CENTER);
        HBox.setMargin(pagination, new Insets(160, 0, 0, 0));

        //Set up Exam VBox
        VBox examVBox = new VBox();
        examVBox.getChildren().add(timerArea);
        examVBox.getChildren().add(questionPane);

        return examVBox;
    }

    private VBox setupSummaryPage(Font toolFont) {
        Pane blankPaneHeader = new Pane();
        blankPaneHeader.setPrefHeight(screenHeight / 4);
        blankPaneHeader.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        Pane blankPaneFooter = new Pane();
        blankPaneFooter.setPrefHeight(screenHeight / 2);
        blankPaneFooter.setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        Label resultTitle = new Label("Your result is:");
        resultTitle.setStyle("-fx-font-size: 48;");
        int correctAnswer = calculateTestingResult();
        String passFailString = determinePassOrFail(correctAnswer);
        Label resultPassFail = new Label(passFailString);
        if (passFailString.equals("Passed")) {
            resultPassFail.setStyle("-fx-text-fill: #5E8C5D; -fx-font-size: 72; -fx-font-weight: bold;");
        } else {
            resultPassFail.setStyle("-fx-text-fill: #F0292A; -fx-font-size: 72; -fx-font-weight: bold;");
        }
        Label resultDashLine = new Label("------------------------------------------------------------------------------------------------------");
        resultDashLine.setFont(toolFont);
        Label resultActualScore = new Label("Correct " + correctAnswer + "/" + questionHandler.numberOfQuestionsPerQuestionBank);
        resultActualScore.setStyle("-fx-font-size: 48;");

        //add command button
        Button returnToHomeButton = new Button("Return Home");
        returnToHomeButton.setFont(toolFont);
        returnToHomeButton.setOnAction(event -> {
            selectedAnswer = new int[40][10];
            questionHandler.isFirstLoad = true;
            try {
                changeStageAndScene(event, setupHomePage(toolFont), "Home Page");
            } catch (IOException | net.lingala.zip4j.exception.ZipException e) {
                throw new RuntimeException(e);
            }
        });
        Button startNewTestButton = new Button("Start New Test");
        startNewTestButton.setOnAction(event -> {
            try {
                selectedAnswer = new int[40][10];
                changeStageAndScene(event, setupLayoutPageExam(toolFont), "Examination Page of: " + selectTestingTypeComboBox.getValue());
            } catch (IOException | net.lingala.zip4j.exception.ZipException e) {
                throw new RuntimeException(e);
            }
        });
        startNewTestButton.setFont(toolFont);
        Button quitAppButton = new Button("Quit");
        quitAppButton.setFont(toolFont);
        quitAppButton.setOnAction(event -> {
            try {
                cleanDirectory(questionHandler.imagesFolder);
                questionHandler.imagesFolder.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ((Stage) (quitAppButton.getScene().getWindow())).close();
        });
        HBox summaryCommandContainer = new HBox();
        summaryCommandContainer.setPrefWidth(screenWidth);
        summaryCommandContainer.setAlignment(Pos.CENTER);
        summaryCommandContainer.getChildren().add(returnToHomeButton);
        summaryCommandContainer.getChildren().add(startNewTestButton);
        summaryCommandContainer.getChildren().add(quitAppButton);
        HBox.setMargin(returnToHomeButton, new Insets(10, 10, 10, 10));
        HBox.setMargin(startNewTestButton, new Insets(10, 10, 10, 10));
        HBox.setMargin(quitAppButton, new Insets(10, 10, 10, 30));

        //Set up Result VBox
        VBox resultVBox = new VBox();
        resultVBox.setPrefWidth(screenWidth);
        resultVBox.setAlignment(Pos.CENTER);
        resultVBox.getChildren().add(blankPaneHeader);
        resultVBox.getChildren().add(resultTitle);
        resultVBox.getChildren().add(resultPassFail);
        resultVBox.getChildren().add(resultDashLine);
        resultVBox.getChildren().add(resultActualScore);
        resultVBox.getChildren().add(summaryCommandContainer);
        resultVBox.getChildren().add(blankPaneFooter);

        return resultVBox;
    }

    private int calculateTestingResult() {
        int calculatedCorrectAnswers = 0;
        for (int i = 0; i < correctAnswer.length; i++) {
            if (Arrays.equals(correctAnswer[i], selectedAnswer[i])) {
                calculatedCorrectAnswers += 1;
            }
        }
        return calculatedCorrectAnswers;
    }

    private String determinePassOrFail(int actualResult) {
        return (actualResult < 26) ? "Failed" : "Passed";
    }

    private String calculateTimeLeft(int[] seconds) {
        int minsLeft = seconds[0] / 60;
        String minsText = "";
        if (minsLeft < 10) {
            minsText = "0" + minsLeft;
        } else {
            minsText = String.valueOf(minsLeft);
        }
        int secsLeft = seconds[0] - minsLeft * 60;
        String secsText = "";
        if (secsLeft < 10) {
            secsText = "0" + secsLeft;
        } else {
            secsText = String.valueOf(secsLeft);
        }
        return minsText + ":" + secsText;
    }

    private ScrollPane getQuestionPages(int pageIndex) {
        //setup and clean up data
        questionTitle = new Label[10];
        questionImage = new javafx.scene.image.ImageView[10];
        questionGridTable = new GridPane[10];
        questionObjects = new Object[10];
        answerCheckBoxes = new CheckBox[10];
        answerRadioButtons = new RadioButton[10];
        answerRadioGroup[pageIndex] = new ToggleGroup();
        answerHBoxContainers = new HBox[5];
        //map question data to local variables
        mapValueFromTestingQuestionToLocalVariablesByPageIndex(pageIndex);
        //Add more question components inside vbox
        Label questionNumber = new Label("Question " + (pageIndex + 1) + ":");
        assignQuestionDataFromClassToTitleLabelOrImage();
        String kindOfChoice = (isQuestionMultipleChoices) ? "[Multi Choice]" : "[Single choice]";
        Label answerLabel = new Label("Answer: " + kindOfChoice);
        assignAnswersDataFromClassToCheckBoxOrRadioButton(pageIndex);
        VBox questionContainer = new VBox();
        questionContainer.getChildren().add(questionNumber);
        addTitleObjectsToVBoxContainer(questionContainer);
        questionContainer.getChildren().add(answerLabel);
        addAnswerHBoxObjectsToVBoxContainer(questionContainer);
        addNavigationButtonsToVBoxContainer(questionContainer, pageIndex);
        VBox.setMargin(answerLabel, new Insets(5, 5, 5, 5));
        VBox.setMargin(questionNumber, new Insets(5, 5, 5, 5));
        //End of question components
        VBox alignmentQuestionVBox = new VBox();
        alignmentQuestionVBox.getChildren().add(questionContainer);
        VBox.setMargin(questionContainer, new Insets(5, 5, 5, 5));
        AnchorPane anchorPaneQuestion = new AnchorPane();
        anchorPaneQuestion.getChildren().add(alignmentQuestionVBox);
        ScrollPane scrollPaneQuestion = new ScrollPane();
        scrollPaneQuestion.setFitToWidth(true);
        scrollPaneQuestion.setPrefViewportHeight(3000);
        scrollPaneQuestion.setPrefHeight(420);
        scrollPaneQuestion.setContent(anchorPaneQuestion);
        return scrollPaneQuestion;
    }

    private void addNavigationButtonsToVBoxContainer(VBox questionContainer, int pageIndex) {
        HBox navigationContainer = new HBox();
        navigationContainer.getChildren().add(previousPageButton);
        navigationContainer.getChildren().add(nextPageButton);
        navigationContainer.getChildren().add(endTestButton);
        navigationContainer.setAlignment(Pos.CENTER);
        HBox.setMargin(previousPageButton, new Insets(10, 5, 5, 5));
        HBox.setMargin(nextPageButton, new Insets(10, 5, 5, 5));
        HBox.setMargin(endTestButton, new Insets(10, 10, 5, 10));
        if (pageIndex == 0) {
            nextPageButton.setDisable(false);
            previousPageButton.setDisable(true);
            endTestButton.setVisible(false);
        } else if (pageIndex == questionHandler.numberOfQuestionsPerQuestionBank - 1) {
            nextPageButton.setDisable(true);
            previousPageButton.setDisable(false);
            endTestButton.setVisible(true);
        } else {
            nextPageButton.setDisable(false);
            previousPageButton.setDisable(false);
            endTestButton.setVisible(false);
        }
        nextPageButton.setOnAction(event -> pagination.setCurrentPageIndex(pageIndex + 1));
        previousPageButton.setOnAction(event -> pagination.setCurrentPageIndex(pageIndex - 1));
        endTestButton.setOnAction(event -> {
            changeStageAndScene(event, setupSummaryPage(toolFont), "Summary Page");
        });
        questionContainer.getChildren().add(navigationContainer);
    }

    private void mapValueFromTestingQuestionToLocalVariablesByPageIndex(int questionIndex) {
        QuestionDataModel testingQuestion = questionHandler.testingQuestions[questionIndex];
        questionStringTitle[0] = testingQuestion.getQuestionTitle1();
        questionStringTitle[1] = testingQuestion.getQuestionTitle2();
        questionStringTitle[2] = testingQuestion.getQuestionTitle3();
        questionStringTitle[3] = testingQuestion.getQuestionTitle4();
        questionStringTitle[4] = testingQuestion.getQuestionTitle5();
        questionStringTitle[5] = testingQuestion.getQuestionTitle6();
        questionStringTitle[6] = testingQuestion.getQuestionTitle7();
        questionStringTitle[7] = testingQuestion.getQuestionTitle8();
        questionStringTitle[8] = testingQuestion.getQuestionTitle9();
        questionStringTitle[9] = testingQuestion.getQuestionTitle10();
        isQuestionMultipleChoices = testingQuestion.isMultipleChoice;
        questionStringAnswer[0] = testingQuestion.getQuestionAnswer1();
        questionStringAnswer[1] = testingQuestion.getQuestionAnswer2();
        questionStringAnswer[2] = testingQuestion.getQuestionAnswer3();
        questionStringAnswer[3] = testingQuestion.getQuestionAnswer4();
        questionStringAnswer[4] = testingQuestion.getQuestionAnswer5();
        questionStringAnswer[5] = testingQuestion.getQuestionAnswer6();
        questionStringAnswer[6] = testingQuestion.getQuestionAnswer7();
        questionStringAnswer[7] = testingQuestion.getQuestionAnswer8();
        questionStringAnswer[8] = testingQuestion.getQuestionAnswer9();
        questionStringAnswer[9] = testingQuestion.getQuestionAnswer10();
        questionBooleanIsAnswerCorrect[0] = testingQuestion.isQuestionAnswer1Correct;
        questionBooleanIsAnswerCorrect[1] = testingQuestion.isQuestionAnswer2Correct;
        questionBooleanIsAnswerCorrect[2] = testingQuestion.isQuestionAnswer3Correct;
        questionBooleanIsAnswerCorrect[3] = testingQuestion.isQuestionAnswer4Correct;
        questionBooleanIsAnswerCorrect[4] = testingQuestion.isQuestionAnswer5Correct;
        questionBooleanIsAnswerCorrect[5] = testingQuestion.isQuestionAnswer6Correct;
        questionBooleanIsAnswerCorrect[6] = testingQuestion.isQuestionAnswer7Correct;
        questionBooleanIsAnswerCorrect[7] = testingQuestion.isQuestionAnswer8Correct;
        questionBooleanIsAnswerCorrect[8] = testingQuestion.isQuestionAnswer9Correct;
        questionBooleanIsAnswerCorrect[9] = testingQuestion.isQuestionAnswer10Correct;
    }

    private void assignQuestionDataFromClassToTitleLabelOrImage() {
        for (int i = 0; i < 10; i++) {
            if (Objects.equals(questionStringTitle[i], "")) {
                break;
            } else if (questionStringTitle[i].contains("Images\\")) {
                questionStringTitle[i] = questionStringTitle[i]
                        .replace("Images\\", "file:///" + questionHandler.imageFolderAbsolutePath);
                questionImage[i] = new ImageView();
                questionImage[i].setImage(new Image(questionStringTitle[i]));
                questionObjects[i] = questionImage[i];
            } else if (questionStringTitle[i].contains("[TableHeader]")) {
                String[] tableRowData = questionStringTitle[i].split("(\\[TableRow\\])");
                questionGridTable[i] = new GridPane();
                questionGridTable[i].setGridLinesVisible(true);
                renderQuestionGridTable(questionGridTable[i], tableRowData);
                questionObjects[i] = questionGridTable[i];
            } else {
                questionTitle[i] = new Label(questionStringTitle[i]);
                questionTitle[i].setPrefWidth(labelWidthInScrollPane);
                questionTitle[i].setWrapText(true);
                questionObjects[i] = questionTitle[i];
            }
        }
    }

    private void renderQuestionGridTable(GridPane gridPane, String[] tableRowData) {
        String[] rowDataStringList;
        for (int rowIndex = 0; rowIndex < tableRowData.length; rowIndex++) {
            tableRowData[rowIndex] = tableRowData[rowIndex].replace("[TableHeader]", "");
            rowDataStringList = tableRowData[rowIndex].split("#");
            renderQuestionGridRow(gridPane, rowDataStringList, rowIndex);
        }
    }

    private void renderQuestionGridRow(GridPane gridPane, String[] rowDataStringList, int rowIndex) {
        Label[] colLabels = new Label[rowDataStringList.length];
        for (int colIndex = 0; colIndex < rowDataStringList.length; colIndex++) {
            colLabels[colIndex] = new Label(rowDataStringList[colIndex]);
            gridPane.add(colLabels[colIndex], colIndex, rowIndex);
            GridPane.setMargin(colLabels[colIndex], new Insets(5, 5, 5, 5));
        }
    }

    private VBox addTitleObjectsToVBoxContainer(VBox questionContainer) {
        for (int i = 0; i < 10; i++) {
            if (questionObjects[i] == null) {
                break;
            } else {
                questionContainer.getChildren().add((Node) questionObjects[i]);
                VBox.setMargin((Node) questionObjects[i], new Insets(2, 2, 2, 5));
            }
        }
        return questionContainer;
    }

    private void assignAnswersDataFromClassToCheckBoxOrRadioButton(int pageIndex) {
        int answerIndex = 0;
        for (int i = 0; i < 5; i++) {
            if (Objects.equals(questionStringAnswer[answerIndex], "")) {
                break;
            } else if (isQuestionMultipleChoices) {
                answerHBoxContainers[i] = new HBox();
                createAnswerCheckBoxElementWithIndexAndAddToHBoxContainer(pageIndex, answerIndex, answerHBoxContainers[i]);
                answerIndex += 1;
                createAnswerCheckBoxElementWithIndexAndAddToHBoxContainer(pageIndex, answerIndex, answerHBoxContainers[i]);
                answerHBoxContainers[i].setAlignment(Pos.CENTER);
                answerIndex += 1;
                coloringBackGroundForHBoxBasedOnIndexOddAndEven(i);
            } else {
                answerHBoxContainers[i] = new HBox();
                createAnswerRadioButtonElementWithIndexAndAddToHBoxContainer(pageIndex, answerIndex, answerHBoxContainers[i]);
                answerIndex += 1;
                createAnswerRadioButtonElementWithIndexAndAddToHBoxContainer(pageIndex, answerIndex, answerHBoxContainers[i]);
                answerHBoxContainers[i].setAlignment(Pos.CENTER);
                answerIndex += 1;
                coloringBackGroundForHBoxBasedOnIndexOddAndEven(i);
            }
        }
    }

    private void coloringBackGroundForHBoxBasedOnIndexOddAndEven(int hboxIndex) {
        if (hboxIndex % 2 == 0) {
            answerHBoxContainers[hboxIndex].setBackground(new Background(new BackgroundFill(Paint.valueOf("#CACACA"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            answerHBoxContainers[hboxIndex].setBackground(new Background(new BackgroundFill(Paint.valueOf("#BFBFBF"), CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    private HBox createAnswerCheckBoxElementWithIndexAndAddToHBoxContainer(int pageIndex, int elementIndex, HBox container) {
        answerCheckBoxes[elementIndex] = new CheckBox(questionStringAnswer[elementIndex]);
        answerCheckBoxes[elementIndex].setPrefWidth(checkBoxWidthInScrollPane);
        answerCheckBoxes[elementIndex].setWrapText(true);
        if (Objects.equals(questionStringAnswer[elementIndex], "")) {
            answerCheckBoxes[elementIndex].setVisible(false);
        }
        if (answerCheckBoxes[elementIndex].isVisible()) {
            if (selectedAnswer[pageIndex][elementIndex] == 1) {
                answerCheckBoxes[elementIndex].setSelected(true);
            }
            answerCheckBoxes[elementIndex].setOnAction(event -> {
                if (answerCheckBoxes[elementIndex].isSelected()) {
                    selectedAnswer[pageIndex][elementIndex] = 1;
                } else {
                    selectedAnswer[pageIndex][elementIndex] = 0;
                }
            });
        }
        container.getChildren().add(answerCheckBoxes[elementIndex]);
        HBox.setMargin(answerCheckBoxes[elementIndex], new Insets(10, 10, 10, 0));
        return container;
    }

    private HBox createAnswerRadioButtonElementWithIndexAndAddToHBoxContainer(int pageIndex, int elementIndex, HBox container) {
        answerRadioButtons[elementIndex] = new RadioButton(questionStringAnswer[elementIndex]);
        answerRadioButtons[elementIndex].setPrefWidth(checkBoxWidthInScrollPane);
        answerRadioButtons[elementIndex].setWrapText(true);
        answerRadioGroup[pageIndex].getToggles().add(answerRadioButtons[elementIndex]);
        if (Objects.equals(questionStringAnswer[elementIndex], "")) {
            answerRadioButtons[elementIndex].setVisible(false);
        }
        if (answerRadioButtons[elementIndex].isVisible()) {
            if (selectedAnswer[pageIndex][elementIndex] == 1) {
                answerRadioButtons[elementIndex].setSelected(true);
            }
            answerRadioButtons[elementIndex].setOnAction(event -> {
                selectedAnswer[pageIndex] = Arrays.stream(selectedAnswer[pageIndex]).map(e -> e = 0).toArray();
                if (answerRadioButtons[elementIndex].isSelected()) {
                    selectedAnswer[pageIndex][elementIndex] = 1;
                }
            });
        }
        container.getChildren().add(answerRadioButtons[elementIndex]);
        HBox.setMargin(answerRadioButtons[elementIndex], new Insets(10, 10, 10, 0));
        return container;
    }

    private VBox addAnswerHBoxObjectsToVBoxContainer(VBox questionContainer) {
        for (int i = 0; i < 5; i++) {
            if (answerHBoxContainers[i] == null) {
                break;
            } else {
                questionContainer.getChildren().add(answerHBoxContainers[i]);
            }
        }
        return questionContainer;
    }
}