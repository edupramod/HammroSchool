package com.hammroschool.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hammroschool.config.AppConfig;
import com.hammroschool.config.DatabaseSupport;
import com.hammroschool.model.auth.UserRole;
import com.hammroschool.model.entity.Mark;
import com.hammroschool.service.MarkService;

public final class MarkServiceImpl implements MarkService {

    private static final MarkServiceImpl INSTANCE = new MarkServiceImpl();

    private final DatabaseSupport db;

    private MarkServiceImpl() {
        AppConfig cfg = AppConfig.getInstance();
        this.db = new DatabaseSupport(
                cfg.getDatabaseUrl(),
                cfg.getDatabaseUsername(),
                cfg.getDatabasePassword(),
                cfg.getDatabaseDriver());
        db.initializeSchemaIfNeeded();
    }

    public static MarkServiceImpl getInstance() {
        return INSTANCE;
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    public synchronized long saveMark(String studentUsername, String subjectName,
                                      String teacherUsername, int score, int fullMarks,
                                      String examType, String remarks) {
        // Delete existing record for same key before inserting (H2 MERGE on non-PK columns)
        String deleteSql = "DELETE FROM marks WHERE student_username = ? AND subject_name = ? "
                         + "AND teacher_username = ? AND exam_type = ?";
        String insertSql = "INSERT INTO marks "
                         + "(student_username, subject_name, teacher_username, score, full_marks, exam_type, remarks) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = db.openConnection()) {
            try (PreparedStatement del = con.prepareStatement(deleteSql)) {
                del.setString(1, studentUsername);
                del.setString(2, subjectName);
                del.setString(3, teacherUsername);
                del.setString(4, examType == null ? "Terminal" : examType);
                del.executeUpdate();
            }
            try (PreparedStatement ins = con.prepareStatement(insertSql,
                                                               new String[]{"ID"})) {
                ins.setString(1, studentUsername);
                ins.setString(2, subjectName);
                ins.setString(3, teacherUsername);
                ins.setInt(4, score);
                ins.setInt(5, fullMarks);
                ins.setString(6, examType == null ? "Terminal" : examType);
                ins.setString(7, remarks == null ? "" : remarks);
                ins.executeUpdate();
                try (ResultSet keys = ins.getGeneratedKeys()) {
                    return keys.next() ? keys.getLong(1) : -1;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save mark", e);
        }
    }

    @Override
    public synchronized void deleteMark(long id) {
        try (Connection con = db.openConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM marks WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete mark id=" + id, e);
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public synchronized List<Mark> getMarksByTeacher(String teacherUsername) {
        return query(
            "SELECT * FROM marks WHERE teacher_username = ? ORDER BY created_at DESC",
            teacherUsername);
    }

    @Override
    public synchronized List<Mark> getMarksByStudentAndTeacher(String studentUsername,
                                                                String teacherUsername) {
        String sql = "SELECT * FROM marks WHERE student_username = ? AND teacher_username = ? "
                   + "ORDER BY subject_name";
        try (Connection con = db.openConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentUsername);
            ps.setString(2, teacherUsername);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query marks", e);
        }
    }

    @Override
    public synchronized List<String> getSubjectsByTeacher(String teacherUsername) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT DISTINCT subject_name FROM marks "
                   + "WHERE teacher_username = ? ORDER BY subject_name";
        try (Connection con = db.openConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, teacherUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query subjects", e);
        }
        return result;
    }

    @Override
    public synchronized List<String> getAllStudentUsernames() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT username FROM user_accounts WHERE role = ? ORDER BY username";
        try (Connection con = db.openConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, UserRole.STUDENT.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query students", e);
        }
        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<Mark> query(String sql, String param) {
        try (Connection con = db.openConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query marks", e);
        }
    }

    private List<Mark> mapAll(ResultSet rs) throws SQLException {
        List<Mark> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Mark mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Mark(
                rs.getLong("id"),
                rs.getString("student_username"),
                rs.getString("subject_name"),
                rs.getString("teacher_username"),
                rs.getInt("score"),
                rs.getInt("full_marks"),
                rs.getString("exam_type"),
                rs.getString("remarks"),
                ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
    }
}
