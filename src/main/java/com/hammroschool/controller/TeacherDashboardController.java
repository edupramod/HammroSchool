package com.hammroschool.controller;

import java.util.List;
import java.util.Locale;

import com.hammroschool.model.entity.Mark;
import com.hammroschool.service.MarkService;
import com.hammroschool.service.impl.MarkServiceImpl;
import com.hammroschool.util.SceneSwitcher;
import com.hammroschool.util.SessionContext;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TeacherDashboardController {

    private final MarkService markService = MarkServiceImpl.getInstance();
    private String teacherUsername;

    // ── Top bar ──────────────────────────────────────────────────────────────
    @FXML private Label  pageTitle;
    @FXML private Label  pageSubtitle;
    @FXML private Label  userInitialsLabel;
    @FXML private Label  userNameLabel;
    @FXML private Button logoutButton;

    // ── Nav buttons (for active-state styling) ───────────────────────────────
    @FXML private Button navDashboardBtn;
    @FXML private Button navMarkSheetBtn;
    @FXML private Button navReportCardBtn;
    @FXML private Button navPerformanceBtn;

    // ── Section panes ────────────────────────────────────────────────────────
    @FXML private VBox dashboardPane;
    @FXML private VBox markSheetPane;
    @FXML private VBox reportCardPane;
    @FXML private VBox performancePane;

    // ── Dashboard stats ──────────────────────────────────────────────────────
    @FXML private Label statStudentsLabel;
    @FXML private Label statMarksLabel;
    @FXML private Label statSubjectsLabel;
    @FXML private TableView<Mark>           recentMarksTable;
    @FXML private TableColumn<Mark, String> rmStudentCol;
    @FXML private TableColumn<Mark, String> rmSubjectCol;
    @FXML private TableColumn<Mark, String> rmExamCol;
    @FXML private TableColumn<Mark, String> rmScoreCol;
    @FXML private TableColumn<Mark, String> rmGradeCol;

    // ── Mark sheet ────────────────────────────────────────────────────────────
    @FXML private ComboBox<String>           msStudentCombo;
    @FXML private TextField                  msSubjectField;
    @FXML private ComboBox<String>           msExamTypeCombo;
    @FXML private TextField                  msScoreField;
    @FXML private TextField                  msFullMarksField;
    @FXML private TextField                  msRemarksField;
    @FXML private Label                      msStatusLabel;
    @FXML private TextField                  msSearchField;
    @FXML private Label                      msSummaryLabel;
    @FXML private TableView<Mark>            markSheetTable;
    @FXML private TableColumn<Mark, String>  msColStudent;
    @FXML private TableColumn<Mark, String>  msColSubject;
    @FXML private TableColumn<Mark, String>  msColExam;
    @FXML private TableColumn<Mark, String>  msColScore;
    @FXML private TableColumn<Mark, String>  msColFullMarks;
    @FXML private TableColumn<Mark, String>  msColGrade;
    @FXML private TableColumn<Mark, String>  msColRemarks;
    @FXML private TableColumn<Mark, Mark>    msColActions;

    // ── Report card ───────────────────────────────────────────────────────────
    @FXML private ComboBox<String>           rcStudentCombo;
    @FXML private VBox                       rcResultPane;
    @FXML private Label                      rcStudentName;
    @FXML private Label                      rcTotalMarks;
    @FXML private Label                      rcPercentage;
    @FXML private Label                      rcGrade;
    @FXML private Label                      rcResult;
    @FXML private TableView<Mark>            rcTable;
    @FXML private TableColumn<Mark, String>  rcColSubject;
    @FXML private TableColumn<Mark, String>  rcColExam;
    @FXML private TableColumn<Mark, String>  rcColScore;
    @FXML private TableColumn<Mark, String>  rcColFullMarks;
    @FXML private TableColumn<Mark, String>  rcColPercentage;
    @FXML private TableColumn<Mark, String>  rcColGrade;
    @FXML private TableColumn<Mark, String>  rcColRemarks;

    // ── Performance ───────────────────────────────────────────────────────────
    @FXML private ComboBox<String>           pfSubjectCombo;
    @FXML private Label                      pfAvgLabel;
    @FXML private Label                      pfHighLabel;
    @FXML private Label                      pfLowLabel;
    @FXML private Label                      pfPassLabel;
    @FXML private TableView<Mark>            performanceTable;
    @FXML private TableColumn<Mark, String>  pfColRank;
    @FXML private TableColumn<Mark, String>  pfColStudent;
    @FXML private TableColumn<Mark, String>  pfColSubject;
    @FXML private TableColumn<Mark, String>  pfColScore;
    @FXML private TableColumn<Mark, String>  pfColPct;
    @FXML private TableColumn<Mark, String>  pfColGrade;
    @FXML private TableColumn<Mark, String>  pfColStatus;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        teacherUsername = SessionContext.getInstance().requireCurrentUser().getUsername();
        userInitialsLabel.setText(initials(teacherUsername));
        userNameLabel.setText(teacherUsername);

        setupTables();

        List<String> students = markService.getAllStudentUsernames();
        msStudentCombo.setItems(FXCollections.observableArrayList(students));
        rcStudentCombo.setItems(FXCollections.observableArrayList(students));
        msExamTypeCombo.setItems(FXCollections.observableArrayList(
                "Terminal", "Mid-Term", "Final", "Unit Test", "Practical"));
        msExamTypeCombo.setValue("Terminal");
        msFullMarksField.setText("100");

        msSearchField.textProperty().addListener((obs, o, n) -> loadMarkSheetTable(n));

        showPane(dashboardPane, navDashboardBtn, "Dashboard", "Welcome to your teacher portal");
        refreshDashboard();
    }

    // ── Nav ───────────────────────────────────────────────────────────────────

    @FXML private void handleNavDashboard() {
        showPane(dashboardPane, navDashboardBtn, "Dashboard", "Welcome to your teacher portal");
        refreshDashboard();
    }

    @FXML private void handleNavMarkSheet() {
        showPane(markSheetPane, navMarkSheetBtn, "Mark Sheet", "Enter and manage student marks");
        loadMarkSheetTable("");
        refreshSubjectCombo();
    }

    @FXML private void handleNavReportCard() {
        showPane(reportCardPane, navReportCardBtn, "Report Card", "Generate academic report cards");
        rcResultPane.setVisible(false);
        rcResultPane.setManaged(false);
    }

    @FXML private void handleNavPerformance() {
        showPane(performancePane, navPerformanceBtn, "Performance Summary",
                "Class-wide performance analysis");
        refreshSubjectCombo();
        handleRefreshPerformance();
    }

    @FXML private void handleLogout() {
        SessionContext.getInstance().clear();
        SceneSwitcher.showView(logoutButton, "/com/hammroschool/hello-view.fxml",
                "Hammro School", 920, 720);
    }

    // ── Mark sheet actions ────────────────────────────────────────────────────

    @FXML
    private void handleSaveMark() {
        String student = msStudentCombo.getValue();
        String subject = msSubjectField.getText().trim();
        String exam    = msExamTypeCombo.getValue();

        if (student == null || subject.isBlank() || exam == null) {
            setMsStatus("Please fill Student, Subject and Exam Type.", false);
            return;
        }
        int score, fullMarks;
        try {
            score     = Integer.parseInt(msScoreField.getText().trim());
            fullMarks = Integer.parseInt(msFullMarksField.getText().trim());
        } catch (NumberFormatException e) {
            setMsStatus("Score and Full Marks must be whole numbers.", false);
            return;
        }
        if (score < 0 || score > fullMarks) {
            setMsStatus("Score must be between 0 and Full Marks.", false);
            return;
        }

        markService.saveMark(student, subject, teacherUsername, score, fullMarks,
                exam, msRemarksField.getText().trim());

        setMsStatus("Mark saved.", true);
        loadMarkSheetTable(msSearchField.getText());
        refreshDashboard();
        refreshSubjectCombo();
    }

    @FXML
    private void handleClearMarkForm() {
        msStudentCombo.setValue(null);
        msSubjectField.clear();
        msExamTypeCombo.setValue("Terminal");
        msScoreField.clear();
        msFullMarksField.setText("100");
        msRemarksField.clear();
        msStatusLabel.setText("");
    }

    // ── Report card ───────────────────────────────────────────────────────────

    @FXML
    private void handleGenerateReportCard() {
        String student = rcStudentCombo.getValue();
        if (student == null) return;

        List<Mark> marks = markService.getMarksByStudentAndTeacher(student, teacherUsername);
        rcStudentName.setText(fmt(student));
        rcTable.setItems(FXCollections.observableArrayList(marks));

        if (marks.isEmpty()) {
            rcTotalMarks.setText("—");
            rcPercentage.setText("—");
            rcGrade.setText("—");
            rcResult.setText("No Data");
            rcResult.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #8a8a8a;");
        } else {
            int scored = marks.stream().mapToInt(Mark::getScore).sum();
            int total  = marks.stream().mapToInt(Mark::getFullMarks).sum();
            double pct = total > 0 ? Math.round(scored * 1000.0 / total) / 10.0 : 0;
            boolean pass = pct >= 40;

            rcTotalMarks.setText(scored + " / " + total);
            rcPercentage.setText(pct + "%");
            rcGrade.setText(gradeFromPct(pct));
            rcResult.setText(pass ? "PASS" : "FAIL");
            rcResult.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: "
                    + (pass ? "#16a34a" : "#dc2626") + ";");
        }
        rcResultPane.setVisible(true);
        rcResultPane.setManaged(true);
    }

    // ── Performance ───────────────────────────────────────────────────────────

    @FXML
    private void handleRefreshPerformance() {
        List<Mark> marks = markService.getMarksByTeacher(teacherUsername);

        String sub = pfSubjectCombo.getValue();
        if (sub != null && !sub.isBlank()) {
            marks = marks.stream()
                    .filter(m -> m.getSubjectName().equalsIgnoreCase(sub))
                    .toList();
        }

        if (marks.isEmpty()) {
            pfAvgLabel.setText("—");
            pfHighLabel.setText("—");
            pfLowLabel.setText("—");
            pfPassLabel.setText("—");
            performanceTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        double avg  = marks.stream().mapToDouble(Mark::getPercentage).average().orElse(0);
        double high = marks.stream().mapToDouble(Mark::getPercentage).max().orElse(0);
        double low  = marks.stream().mapToDouble(Mark::getPercentage).min().orElse(0);
        long   pass = marks.stream().filter(m -> m.getPercentage() >= 40).count();

        pfAvgLabel.setText(String.format("%.1f%%", avg));
        pfHighLabel.setText(String.format("%.1f%%", high));
        pfLowLabel.setText(String.format("%.1f%%", low));
        pfPassLabel.setText(pass + " / " + marks.size());

        List<Mark> ranked = marks.stream()
                .sorted((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()))
                .toList();
        performanceTable.setItems(FXCollections.observableArrayList(ranked));
    }

    // ── Table setup ───────────────────────────────────────────────────────────

    private void setupTables() {
        // Recent marks (dashboard)
        rmStudentCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(fmt(c.getValue().getStudentUsername())));
        rmSubjectCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSubjectName()));
        rmExamCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getExamType()));
        rmScoreCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getScore() + " / " + c.getValue().getFullMarks()));
        rmGradeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getGrade()));
        setupGradeBadgeColumn(rmGradeCol);
        recentMarksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        recentMarksTable.setPlaceholder(new Label("No marks entered yet."));

        // Mark sheet table
        msColStudent.setCellValueFactory(c -> new ReadOnlyStringWrapper(fmt(c.getValue().getStudentUsername())));
        msColSubject.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSubjectName()));
        msColExam.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getExamType()));
        msColScore.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getScore())));
        msColFullMarks.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getFullMarks())));
        msColGrade.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getGrade()));
        msColRemarks.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getRemarks() == null ? "" : c.getValue().getRemarks()));
        setupGradeBadgeColumn(msColGrade);

        msColActions.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        msColActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; "
                        + "-fx-font-size: 12px; -fx-font-weight: 700; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
                btn.setOnAction(e -> {
                    Mark m = getItem();
                    if (m != null) {
                        markService.deleteMark(m.getId());
                        loadMarkSheetTable(msSearchField.getText());
                        refreshDashboard();
                    }
                });
            }
            @Override protected void updateItem(Mark m, boolean empty) {
                super.updateItem(m, empty);
                setGraphic(empty || m == null ? null : btn);
            }
        });
        markSheetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        markSheetTable.setPlaceholder(new Label("No marks recorded yet."));

        rcColSubject.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSubjectName()));
        rcColExam.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getExamType()));
        rcColScore.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getScore())));
        rcColFullMarks.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getFullMarks())));
        rcColPercentage.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getPercentage() + "%"));
        rcColGrade.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getGrade()));
        rcColRemarks.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getRemarks() == null ? "" : c.getValue().getRemarks()));
        setupGradeBadgeColumn(rcColGrade);
        rcTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        rcTable.setPlaceholder(new Label("No marks found for this student."));

        pfColRank.setCellValueFactory(c -> {
            int idx = performanceTable.getItems().indexOf(c.getValue());
            return new ReadOnlyStringWrapper(String.valueOf(idx + 1));
        });
        pfColStudent.setCellValueFactory(c -> new ReadOnlyStringWrapper(fmt(c.getValue().getStudentUsername())));
        pfColSubject.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSubjectName()));
        pfColScore.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getScore() + " / " + c.getValue().getFullMarks()));
        pfColPct.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getPercentage() + "%"));
        pfColGrade.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getGrade()));
        pfColStatus.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getPercentage() >= 40 ? "Pass" : "Fail"));
        pfColStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(s);
                boolean pass = "Pass".equals(s);
                badge.setStyle("-fx-background-color: " + (pass ? "#dcfce7" : "#fee2e2") + "; "
                        + "-fx-text-fill: " + (pass ? "#16a34a" : "#dc2626") + "; "
                        + "-fx-padding: 3 10 3 10; -fx-background-radius: 999; "
                        + "-fx-font-size: 11px; -fx-font-weight: 800;");
                setGraphic(badge); setText(null);
            }
        });
        setupGradeBadgeColumn(pfColGrade);
        performanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        performanceTable.setPlaceholder(new Label("No performance data available."));
    }

    private void refreshDashboard() {
        List<Mark> marks   = markService.getMarksByTeacher(teacherUsername);
        List<String> studs = markService.getAllStudentUsernames();
        List<String> subs  = markService.getSubjectsByTeacher(teacherUsername);

        statStudentsLabel.setText(String.valueOf(studs.size()));
        statMarksLabel.setText(String.valueOf(marks.size()));
        statSubjectsLabel.setText(String.valueOf(subs.size()));

        recentMarksTable.setItems(FXCollections.observableArrayList(
                marks.stream().limit(10).toList()));
    }

    private void loadMarkSheetTable(String query) {
        List<Mark> all = markService.getMarksByTeacher(teacherUsername);
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Mark> filtered = q.isBlank() ? all
                : all.stream().filter(m ->
                        m.getStudentUsername().toLowerCase(Locale.ROOT).contains(q)
                        || m.getSubjectName().toLowerCase(Locale.ROOT).contains(q))
                .toList();
        markSheetTable.setItems(FXCollections.observableArrayList(filtered));
        msSummaryLabel.setText(filtered.size() + " record" + (filtered.size() == 1 ? "" : "s"));
    }

    private void refreshSubjectCombo() {
        List<String> subjects = markService.getSubjectsByTeacher(teacherUsername);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("");        
        items.addAll(subjects);
        pfSubjectCombo.setItems(items);
    }


    private static final String ACTIVE_STYLE =
            "-fx-background-color: #111111; -fx-background-radius: 8; -fx-text-fill: white; "
            + "-fx-font-size: 13px; -fx-font-weight: 700; -fx-padding: 10 14 10 14; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: #272727; "
            + "-fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10 14 10 14; -fx-cursor: hand;";

    private void showPane(VBox target, Button activeBtn, String title, String subtitle) {
        for (VBox pane : List.of(dashboardPane, markSheetPane, reportCardPane, performancePane)) {
            pane.setVisible(pane == target);
            pane.setManaged(pane == target);
        }
        for (Button btn : List.of(navDashboardBtn, navMarkSheetBtn,
                                   navReportCardBtn, navPerformanceBtn)) {
            btn.setStyle(btn == activeBtn ? ACTIVE_STYLE : INACTIVE_STYLE);
        }
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
    }

    private void setMsStatus(String msg, boolean ok) {
        msStatusLabel.setText(msg);
        msStatusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: "
                + (ok ? "#16a34a" : "#dc2626") + ";");
    }

    private <T> void setupGradeBadgeColumn(TableColumn<T, String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String grade, boolean empty) {
                super.updateItem(grade, empty);
                if (empty || grade == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(grade);
                badge.setStyle("-fx-background-color: " + gradeBadgeColor(grade)
                        + "; -fx-text-fill: white; -fx-padding: 3 10 3 10; "
                        + "-fx-background-radius: 999; -fx-font-size: 11px; -fx-font-weight: 800;");
                setGraphic(badge); setText(null);
            }
        });
    }

    private String gradeBadgeColor(String g) {
        return switch (g) {
            case "A+" -> "#059669";
            case "A"  -> "#16a34a";
            case "B+" -> "#2563eb";
            case "B"  -> "#3b82f6";
            case "C"  -> "#d97706";
            case "D"  -> "#ea580c";
            default   -> "#dc2626";
        };
    }

    private String gradeFromPct(double pct) {
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        return p.length == 1
                ? p[0].substring(0, Math.min(2, p[0].length())).toUpperCase(Locale.ROOT)
                : (p[0].substring(0, 1) + p[1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private String fmt(String username) {
        if (username == null || username.isBlank()) return "Unknown";
        String t = username.trim();
        return Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }
}
