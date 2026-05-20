package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Patient;
import models.Appointment;
import models.Consultation;
import services.PatientService;
import services.AppointmentService;
import services.ConsultationService;
import exceptions.AppointmentConflictException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import services.EmailService;
import java.io.File;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Dashboard extends Application {

    private ObservableList<Patient> patientList;
    private PatientService patientService;
    private AppointmentService appointmentService;
    private ConsultationService consultationService;
    private TableView<Patient> patientTable;
    private Label patientCountLabel;
    private Label appointmentCountLabel;
    private Label consultationCountLabel;

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField phoneField;
    private TextField ageField;
    private TextField emailField;
    private TextField searchField;
    private Label resultLabel;

    @Override
    public void start(Stage stage) {
        patientService = new PatientService();
        appointmentService = new AppointmentService();
        consultationService = new ConsultationService();
        patientList = FXCollections.observableArrayList();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #0b1220;");
        mainLayout.setPadding(new Insets(20));

        mainLayout.setTop(createHeader());

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.35);
        splitPane.setStyle("-fx-background-color: transparent;");

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        splitPane.getItems().addAll(leftPanel, rightPanel);
        mainLayout.setCenter(splitPane);

        Scene scene = new Scene(mainLayout, 1400, 800);
        stage.setTitle("Medical Cabinet Management");
        stage.setScene(scene);
        stage.show();

        refreshTable();
        updateAllCounts();
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label title = new Label("🏥 Medical Cabinet Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Professional Hospital Dashboard");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.LIGHTGRAY);

        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER_LEFT);
        statsCards.setPadding(new Insets(15, 0, 0, 0));

        patientCountLabel = new Label("0");
        appointmentCountLabel = new Label("0");
        consultationCountLabel = new Label("0");

        VBox patientCard = createStatsCard("Total Patients", patientCountLabel, "#3b82f6");
        VBox appointmentCard = createStatsCard("Total Appointments", appointmentCountLabel, "#8b5cf6");
        VBox consultationCard = createStatsCard("Total Consultations", consultationCountLabel, "#10b981");

        statsCards.getChildren().addAll(patientCard, appointmentCard, consultationCard);

        header.getChildren().addAll(title, subtitle, statsCards);
        return header;
    }

    private VBox createStatsCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15, 25, 15, 25));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 15;");

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", 12));

        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(20);
        leftPanel.setPadding(new Insets(20));
        leftPanel.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");;

        Label formTitle = new Label("Patient Information");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.WHITE);

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(10, 0, 10, 0));

        firstNameField = createStyledField("First Name");
        lastNameField = createStyledField("Last Name");
        phoneField = createStyledField("Phone");
        ageField = createStyledField("Age");
        emailField = createStyledField("Email");

        addFormRow(form, "First Name", firstNameField, 0);
        addFormRow(form, "Last Name", lastNameField, 1);
        addFormRow(form, "Phone", phoneField, 2);
        addFormRow(form, "Age", ageField, 3);
        addFormRow(form, "Email", emailField, 4);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = createStyledButton("➕ Add", "#1d4ed8");
        Button updateButton = createStyledButton("✏️ Update", "#2563eb");
        Button deleteButton = createStyledButton("🗑️ Delete", "#1e3a8a");
        Button clearButton = createStyledButton("🧹 Clear", "#3b82f6");

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        resultLabel = new Label();
        resultLabel.setTextFill(Color.LIGHTGREEN);
        resultLabel.setFont(Font.font("Arial", 12));

        leftPanel.getChildren().addAll(formTitle, form, buttonBox, resultLabel);

        addButton.setOnAction(e -> addPatient());
        updateButton.setOnAction(e -> updatePatient());
        deleteButton.setOnAction(e -> deletePatient());
        clearButton.setOnAction(e -> clearForm());

        return leftPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("🔍 Search:");
        searchLabel.setTextFill(Color.WHITE);
        searchLabel.setFont(Font.font("Arial", 14));

        searchField = createStyledField("Search patient by name or phone...");
        searchField.setPrefWidth(400);
        searchField.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                refreshTable();
            } else {
                patientList.setAll(patientService.searchPatients(newVal));
                updateAllCounts();
            }
        });

        searchBox.getChildren().addAll(searchLabel, searchField);

        patientTable = new TableView<>();
        patientTable.setStyle("""
    -fx-background-color: #111c2e;
    -fx-text-fill: #e5e7eb;
    -fx-control-inner-background: #111c2e;
    -fx-table-cell-border-color: #23324a;
""");
        patientTable.setPlaceholder(new Label("No patients found"));

        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Patient, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(120);

        TableColumn<Patient, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(120);

        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(130);

        TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(70);

        TableColumn<Patient, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);

        actionsCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("👁 View");
            private final Button pdfButton = new Button("📄 PDF");
            private final Button emailButton = new Button("✉ Email");

            {
                // VIEW BUTTON
                viewButton.setStyle(
                        "-fx-background-color: #3b82f6;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 12px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-cursor: hand;"
                );

                viewButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showPatientHistory(patient);
                });

                // PDF BUTTON
                pdfButton.setStyle(
                        "-fx-background-color: #10b981;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 12px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-cursor: hand;"
                );

                pdfButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    exportPatientPDF(patient);
                });
                //email button
                emailButton.setStyle(
                        "-fx-background-color: #f59e0b;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 12px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 5 10 5 10;" +
                                "-fx-cursor: hand;"
                );
                emailButton.setOnAction(event -> {

                    Patient patient = getTableView().getItems().get(getIndex());

                    File pdfFile = generateTempPatientPDF(patient);

                    boolean sent = EmailService.sendReportToPatient(patient, pdfFile);

                    if (sent) {
                        showSuccess("Report sent to " + patient.getEmail());
                    } else {
                        showError("Failed to send email");
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, viewButton, pdfButton, emailButton);
                    setGraphic(box);
                }
            }
        });

        patientTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, phoneCol, ageCol, actionsCol);
        patientTable.setItems(patientList);
        patientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selected) -> {
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                phoneField.setText(selected.getPhone());
                ageField.setText(String.valueOf(selected.getAge()));
                emailField.setText(selected.getEmail());
            }
        });

        rightPanel.getChildren().addAll(searchBox, patientTable);
        return rightPanel;
    }

    private int writeLine(PDPageContentStream content, String text, int y) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(50, y);

        if (text.length() > 100) {
            text = text.substring(0, 100) + "...";
        }

        content.showText(text);
        content.endText();

        return y - 15;
    }


    private void exportPatientPDF(Patient patient) {
        if (patient == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Patient PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            int y = 750;

            // ================= HEADER =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(50, y);
            content.showText("MEDICAL PATIENT REPORT");
            content.endText();

            y -= 40;

            // ================= PATIENT INFO =================
            y = writeLine(content, "Patient ID: " + patient.getId(), y);
            y = writeLine(content, "Name: " + patient.getFirstName() + " " + patient.getLastName(), y);
            y = writeLine(content, "Phone: " + patient.getPhone(), y);
            y = writeLine(content, "Age: " + patient.getAge(), y);

            y -= 20;

            // ================= APPOINTMENTS =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(50, y);
            content.showText("APPOINTMENT HISTORY:");
            content.endText();

            y -= 25;

            List<Appointment> appointments =
                    appointmentService.getAppointmentsByPatientId(patient.getId());

            if (appointments.isEmpty()) {
                y = writeLine(content, "No appointments found.", y);
            } else {
                for (Appointment a : appointments) {

                    y = writeLine(content,
                            "Date: " + a.getAppointmentDate() + " | Time: " + a.getAppointmentTime(),
                            y);

                    y = writeLine(content,
                            "Reason: " + a.getReason(),
                            y);

                    y -= 10;
                }

            }

            y -= 20;

            // ================= CONSULTATIONS =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(50, y);
            content.showText("CONSULTATION HISTORY:");
            content.endText();

            y -= 25;

            List<Consultation> consultations =
                    consultationService.getConsultationsByPatientId(patient.getId());

            if (consultations.isEmpty()) {
                y = writeLine(content, "No consultations found.", y);
            } else {
                for (Consultation c : consultations) {

                    y = writeLine(content,
                            "Date: " + c.getConsultationDate(),
                            y);

                    y = writeLine(content,
                            "Diagnosis: " + c.getDiagnosis(),
                            y);

                    y = writeLine(content,
                            "Prescription: " + c.getPrescription(),
                            y);

                    y = writeLine(content,
                            "Notes: " + c.getNotes(),
                            y);

                    y -= 10;
                }
            }

            content.close();
            document.save(file);

            showSuccess("Full medical report exported!");

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error creating PDF");
        }
    }

    private File generateTempPatientPDF(Patient patient) {
        try {
            File tempFile = File.createTempFile("patient_" + patient.getId(), ".pdf");
            System.out.println("[PDF] Creating temp file: " + tempFile.getAbsolutePath());

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            int y = 750;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(50, y);
            content.showText("MEDICAL REPORT");
            content.endText();
            y -= 40;

            y = writeLine(content, "Name: " + clean(patient.getFirstName() + " " + patient.getLastName()), y);
            y = writeLine(content, "Phone: " + clean(patient.getPhone()), y);
            y = writeLine(content, "Age: " + patient.getAge(), y);
            y -= 20;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 13);
            content.newLineAtOffset(50, y);
            content.showText("APPOINTMENT HISTORY:");
            content.endText();
            y -= 20;

            List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patient.getId());
            System.out.println("[PDF] Appointments found: " + appointments.size());
            if (appointments.isEmpty()) {
                y = writeLine(content, "No appointments found.", y);
            } else {
                for (Appointment a : appointments) {
                    y = writeLine(content, "Date: " + clean(a.getAppointmentDate()) + " | Time: " + clean(a.getAppointmentTime()), y);
                    y = writeLine(content, "Reason: " + clean(a.getReason()), y);
                    y -= 8;
                    if (y < 50) break;
                }
            }
            y -= 20;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 13);
            content.newLineAtOffset(50, y);
            content.showText("CONSULTATION HISTORY:");
            content.endText();
            y -= 20;

            List<Consultation> consultations = consultationService.getConsultationsByPatientId(patient.getId());
            System.out.println("[PDF] Consultations found: " + consultations.size());
            if (consultations.isEmpty()) {
                y = writeLine(content, "No consultations found.", y);
            } else {
                for (Consultation c : consultations) {
                    y = writeLine(content, "Date: " + clean(c.getConsultationDate()), y);
                    y = writeLine(content, "Diagnosis: " + clean(c.getDiagnosis()), y);
                    y = writeLine(content, "Prescription: " + clean(c.getPrescription()), y);
                    y = writeLine(content, "Notes: " + clean(c.getNotes()), y);
                    y -= 8;
                    if (y < 50) break;
                }
            }

            content.close();
            document.save(tempFile);
            document.close();

            System.out.println("[PDF] File saved, size: " + tempFile.length() + " bytes");
            return tempFile;

        } catch (Exception e) {
            System.err.println("[PDF] FAILED TO CREATE PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // strips non-latin characters that PDFBox basic fonts can't handle
    private String clean(String text) {
        if (text == null) return "";
        return text.replaceAll("[^\\x20-\\x7E]", "?");
    }

    private void showPatientHistory(Patient patient) {
        Stage historyStage = new Stage();
        historyStage.setTitle("Patient History - " + patient.getFirstName() + " " + patient.getLastName());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setPadding(new Insets(20));

        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label nameLabel = new Label(patient.getFirstName() + " " + patient.getLastName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.WHITE);

        Label infoLabel = new Label("📞 Phone: " + patient.getPhone() + "  |  🎂 Age: " + patient.getAge());
        infoLabel.setTextFill(Color.LIGHTGRAY);
        infoLabel.setFont(Font.font("Arial", 13));

        header.getChildren().addAll(nameLabel, infoLabel);

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1e293b;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab consultationsTab = new Tab("📋 Consultations");
        consultationsTab.setClosable(false);
        VBox consultationsContent = createConsultationsPanel(patient);
        consultationsTab.setContent(consultationsContent);

        Tab appointmentsTab = new Tab("📅 Appointments");
        appointmentsTab.setClosable(false);
        VBox appointmentsContent = createAppointmentsPanel(patient);
        appointmentsTab.setContent(appointmentsContent);

        tabPane.getTabs().addAll(consultationsTab, appointmentsTab);

        root.setTop(header);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 700);
        historyStage.setScene(scene);
        historyStage.show();

        // Refresh counts when window is closed
        historyStage.setOnHiding(event -> updateAllCounts());
    }

    private VBox createConsultationsPanel(Patient patient) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1e293b;");

        Label title = new Label("Medical Consultations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        // Add New Consultation Section
        TitledPane addPane = new TitledPane();
        addPane.setText("➕ Add New Consultation");
        addPane.setExpanded(false);
        addPane.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-background-color: #2d3a4e;");

        DatePicker consultationDate = new DatePicker(LocalDate.now());
        consultationDate.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");;

        TextField diagnosisField = createStyledField("Diagnosis");
        TextField prescriptionField = createStyledField("Prescription");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Consultation notes...");
        notesArea.setPrefRowCount(3);
        notesArea.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");

        Button addBtn = createStyledButton("💾 Save Consultation", "#10b981");

        Label formStatus = new Label();
        formStatus.setTextFill(Color.LIGHTGREEN);

        form.add(createLabel("Date:"), 0, 0);
        form.add(consultationDate, 1, 0);
        form.add(createLabel("Diagnosis:"), 0, 1);
        form.add(diagnosisField, 1, 1);
        form.add(createLabel("Prescription:"), 0, 2);
        form.add(prescriptionField, 1, 2);
        form.add(createLabel("Notes:"), 0, 3);
        form.add(notesArea, 1, 3);
        form.add(addBtn, 1, 4);
        form.add(formStatus, 1, 5);

        GridPane.setColumnSpan(notesArea, 2);
        GridPane.setColumnSpan(addBtn, 1);

        addPane.setContent(form);

        // Consultations List
        Label historyTitle = new Label("Consultation History");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        historyTitle.setTextFill(Color.WHITE);

        ListView<String> consultationsList = new ListView<>();
        consultationsList.setStyle("-fx-background-color: -fx-background-color: #16243a;\n" +
                "-fx-text-fill: #e5e7eb;\n" +
                "-fx-prompt-text-fill: #94a3b8;\n" +
                "-fx-background-radius: 8;\n" +
                "-fx-border-color: #23324a;; -fx-text-fill: white; -fx-font-size: 13px;");
        consultationsList.setPrefHeight(350);

        // Load existing consultations
        refreshConsultationsList(patient.getId(), consultationsList);

        // Add button action
        addBtn.setOnAction(e -> {
            if (!notesArea.getText().trim().isEmpty()) {
                Consultation consultation = new Consultation(
                        patient.getId(),
                        consultationDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        notesArea.getText(),
                        diagnosisField.getText(),
                        prescriptionField.getText()
                );
                consultationService.addConsultation(consultation);
                refreshConsultationsList(patient.getId(), consultationsList);
                updateAllCounts();

                notesArea.clear();
                diagnosisField.clear();
                prescriptionField.clear();
                consultationDate.setValue(LocalDate.now());

                formStatus.setText("✓ Consultation added successfully!");
                formStatus.setTextFill(Color.LIGHTGREEN);
                addPane.setExpanded(false);

                new Thread(() -> {
                    try { Thread.sleep(3000); } catch (InterruptedException ex) {}
                    javafx.application.Platform.runLater(() -> formStatus.setText(""));
                }).start();
            } else {
                formStatus.setText("✗ Please enter consultation notes");
                formStatus.setTextFill(Color.RED);
            }
        });

        content.getChildren().addAll(title, addPane, historyTitle, consultationsList);
        return content;
    }

    private void refreshConsultationsList(int patientId, ListView<String> listView) {
        listView.getItems().clear();
        List<Consultation> consultations = consultationService.getConsultationsByPatientId(patientId);

        if (consultations.isEmpty()) {
            listView.getItems().add("📭 No consultations recorded yet.");
        } else {
            for (Consultation c : consultations) {
                String display = "📋 " + c.getConsultationDate() + "\n";
                if (!c.getDiagnosis().isEmpty()) {
                    display += "   🔬 Diagnosis: " + c.getDiagnosis() + "\n";
                }
                if (!c.getPrescription().isEmpty()) {
                    display += "   💊 Prescription: " + c.getPrescription() + "\n";
                }
                display += "   📝 Notes: " + c.getNotes();
                listView.getItems().add(display);
            }
        }
    }



    private VBox createAppointmentsPanel(Patient patient) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1e293b;");

        Label title = new Label("📅 Appointments");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        // Schedule New Appointment Section
        TitledPane addPane = new TitledPane();
        addPane.setText("➕ Schedule New Appointment");
        addPane.setExpanded(true);
        addPane.setStyle("-fx-background-color: #2d3a4e; -fx-text-fill: white;");

        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(15));
        formContainer.setStyle("-fx-background-color: #2d3a4e;");

        // Date picker
        HBox dateBox = new HBox(10);
        Label dateLabel = new Label("Date:");
        dateLabel.setTextFill(Color.WHITE);
        DatePicker appointmentDate = new DatePicker(LocalDate.now());
        appointmentDate.setStyle("-fx-background-color: -fx-background-color: #16243a;\n" +
                "-fx-text-fill: #e5e7eb;\n" +
                "-fx-prompt-text-fill: #94a3b8;\n" +
                "-fx-background-radius: 8;\n" +
                "-fx-border-color: #23324a;; -fx-text-fill: white;");
        dateBox.getChildren().addAll(dateLabel, appointmentDate);

        // Time combo box
        HBox timeBox = new HBox(10);
        Label timeLabel = new Label("Time:");
        timeLabel.setTextFill(Color.WHITE);
        ComboBox<String> appointmentTime = new ComboBox<>();
        appointmentTime.getItems().addAll("09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30");
        appointmentTime.setPromptText("Select Time");
        appointmentTime.setStyle("-fx-background-color: -fx-background-color: #16243a;\n" +
                "-fx-text-fill: #e5e7eb;\n" +
                "-fx-prompt-text-fill: #94a3b8;\n" +
                "-fx-background-radius: 8;\n" +
                "-fx-border-color: #23324a;; -fx-text-fill: white;");
        timeBox.getChildren().addAll(timeLabel, appointmentTime);

        // Reason
        VBox reasonBox = new VBox(5);
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setTextFill(Color.BLACK);
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for appointment");
        reasonArea.setPrefRowCount(3);
        reasonArea.setStyle("""
    -fx-control-inner-background: #16243a;
    -fx-background-color: #16243a;

    -fx-text-fill: #e5e7eb;
    -fx-prompt-text-fill: #94a3b8;

    -fx-border-color: #23324a;
    -fx-border-radius: 8;
    -fx-background-radius: 8;

    -fx-focus-color: #3b82f6;
    -fx-faint-focus-color: transparent;
""");
        reasonBox.getChildren().addAll(reasonLabel, reasonArea);

        // Schedule button
        Button scheduleBtn = new Button("📅 Schedule Appointment");
        scheduleBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;");

        // Status label
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.LIGHTGREEN);

        formContainer.getChildren().addAll(dateBox, timeBox, reasonBox, scheduleBtn, statusLabel);
        addPane.setContent(formContainer);

        // Appointments List
        Label appointmentsTitle = new Label("Scheduled Appointments");
        appointmentsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        appointmentsTitle.setTextFill(Color.WHITE);

        ListView<String> appointmentsList = new ListView<>();
        appointmentsList.setStyle("-fx-background-color: -fx-background-color: #16243a;\n" +
                "-fx-text-fill: #e5e7eb;\n" +
                "-fx-prompt-text-fill: #94a3b8;\n" +
                "-fx-background-radius: 8;\n" +
                "-fx-border-color: #23324a;; -fx-text-fill: white; -fx-font-size: 13px;");
        appointmentsList.setPrefHeight(350);

        // Load existing appointments
        refreshAppointmentsList(patient.getId(), appointmentsList);

        // Schedule button action
        scheduleBtn.setOnAction(e -> {
            if (appointmentDate.getValue() != null && appointmentTime.getValue() != null && !reasonArea.getText().trim().isEmpty()) {
                Appointment appointment = new Appointment(
                        patient.getId(),
                        patient.getFirstName() + " " + patient.getLastName(),
                        appointmentDate.getValue().toString(),
                        appointmentTime.getValue(),
                        reasonArea.getText()
                );

                boolean success = appointmentService.addAppointment(appointment);

                if (success) {
                    refreshAppointmentsList(patient.getId(), appointmentsList);
                    updateAllCounts();

                    statusLabel.setText("✅ Appointment scheduled successfully!");
                    statusLabel.setTextFill(Color.LIGHTGREEN);

                    appointmentDate.setValue(LocalDate.now().plusDays(1));
                    appointmentTime.setValue(null);
                    reasonArea.clear();

                    new Thread(() -> {
                        try { Thread.sleep(3000); } catch (InterruptedException ex) {}
                        javafx.application.Platform.runLater(() -> statusLabel.setText(""));
                    }).start();
                } else {
                    statusLabel.setText("❌ Failed to schedule appointment");
                    statusLabel.setTextFill(Color.RED);
                }
            } else {
                statusLabel.setText("❌ Please fill all fields");
                statusLabel.setTextFill(Color.RED);
            }
        });

        content.getChildren().addAll(title, addPane, appointmentsTitle, appointmentsList);
        return content;
    }

    private void refreshAppointmentsList(int patientId, ListView<String> listView) {
        listView.getItems().clear();
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);

        if (appointments.isEmpty()) {
            listView.getItems().add("📭 No appointments scheduled.");
        } else {
            for (Appointment a : appointments) {
                String display = "📅 " + a.getAppointmentDate() + " at " + a.getAppointmentTime() + "\n";
                display += "   📝 Reason: " + a.getReason();
                listView.getItems().add(display);
            }
        }
    }





    private void updateAllCounts() {
        List<Patient> allPatients = patientService.getAllPatients();
        int patientCount = allPatients.size();

        int totalAppointments = 0;
        int totalConsultations = 0;

        for (Patient patient : allPatients) {
            totalAppointments += appointmentService.getAppointmentsByPatientId(patient.getId()).size();
            totalConsultations += consultationService.getConsultationsByPatientId(patient.getId()).size();
        }

        patientCountLabel.setText(String.valueOf(patientCount));
        appointmentCountLabel.setText(String.valueOf(totalAppointments));
        consultationCountLabel.setText(String.valueOf(totalConsultations));
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 13));
        return label;
    }

    private void addPatient() {
        try {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() || ageField.getText().isEmpty()) {
                showError("Please fill all fields");
                return;
            }

            int age = Integer.parseInt(ageField.getText());
            if (age < 0 || age > 150) {
                showError("Please enter a valid age (0-150)");
                return;
            }

            Patient patient = new Patient(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    phoneField.getText(),
                    Integer.parseInt(ageField.getText()),
                    emailField.getText()   // ✅ ADD THIS
            );

            patientService.addPatient(patient);
            refreshTable();
            updateAllCounts();
            clearForm();
            showSuccess("✓ Patient added successfully");

        } catch (NumberFormatException ex) {
            showError("✗ Please enter a valid age");
        } catch (Exception ex) {
            showError("✗ Error adding patient");
        }
    }

    private void updatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                selected.setFirstName(firstNameField.getText());
                selected.setLastName(lastNameField.getText());
                selected.setPhone(phoneField.getText());
                selected.setAge(Integer.parseInt(ageField.getText()));
                selected.setEmail(emailField.getText());

                patientService.updatePatient(selected);
                refreshTable();
                updateAllCounts();
                showSuccess("✓ Patient updated successfully");

            } catch (NumberFormatException ex) {
                showError("✗ Please enter a valid age");
            }
        } else {
            showError("✗ Please select a patient to update");
        }
    }

    private void deletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Patient");
            confirm.setContentText("Are you sure you want to delete " + selected.getFirstName() + " " + selected.getLastName() + "?\n\nThis will also delete all their consultations and appointments.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                patientService.deletePatient(selected.getId());
                refreshTable();
                updateAllCounts();
                clearForm();
                showSuccess("✓ Patient deleted successfully");
            }
        } else {
            showError("✗ Please select a patient to delete");
        }
    }

    private void refreshTable() {
        patientList.setAll(patientService.getAllPatients());
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        ageField.clear();
        patientTable.getSelectionModel().clearSelection();
        resultLabel.setText("");
    }

    private TextField createStyledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: -fx-background-color: #16243a;\n" +
                "-fx-text-fill: #e5e7eb;\n" +
                "-fx-prompt-text-fill: #94a3b8;\n" +
                "-fx-background-radius: 8;\n" +
                "-fx-border-color: #23324a;; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 10;");
        return field;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);

        button.setStyle("""
        -fx-background-color: #16243a;
        -fx-text-fill: white;
        -fx-font-size: 13px;
        -fx-font-weight: bold;

        -fx-border-color: %s;
        -fx-border-width: 2;
        -fx-border-radius: 10;

        -fx-background-radius: 10;
        -fx-padding: 8 14 8 14;
        -fx-cursor: hand;
    """.formatted(color));

        // Hover effect (clean + safe)
        button.setOnMouseEntered(e ->
                button.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;

                -fx-border-color: %s;
                -fx-border-width: 2;
                -fx-border-radius: 10;

                -fx-background-radius: 10;
                -fx-padding: 8 14 8 14;
                -fx-cursor: hand;
            """.formatted(color, color))
        );

        button.setOnMouseExited(e ->
                button.setStyle("""
                -fx-background-color: #16243a;
                -fx-text-fill: white;

                -fx-border-color: %s;
                -fx-border-width: 2;
                -fx-border-radius: 10;

                -fx-background-radius: 10;
                -fx-padding: 8 14 8 14;
                -fx-cursor: hand;
            """.formatted(color))
        );

        return button;
    }

    private void addFormRow(GridPane form, String labelText, javafx.scene.Node field, int row) {
        Label label = new Label(labelText + ":");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 13));
        form.add(label, 0, row);
        form.add(field, 1, row);
        GridPane.setColumnSpan(field, 2);
    }

    private void showSuccess(String message) {
        resultLabel.setText(message);
        resultLabel.setTextFill(Color.LIGHTGREEN);
        resultLabel.setStyle("-fx-font-size: 13px;");

        // Clear after 3 seconds
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
            javafx.application.Platform.runLater(() -> {
                if (resultLabel.getText().equals(message)) {
                    resultLabel.setText("");
                }
            });
        }).start();
    }

    private void showError(String message) {
        resultLabel.setText(message);
        resultLabel.setTextFill(Color.RED);
        resultLabel.setStyle("-fx-font-size: 13px;");
    }
}