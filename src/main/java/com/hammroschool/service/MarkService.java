package com.hammroschool.service;

import java.util.List;

import com.hammroschool.model.entity.Mark;

public interface MarkService {

    /** Save or update a mark (MERGE on student+subject+teacher+examType). Returns generated id. */
    long saveMark(String studentUsername, String subjectName, String teacherUsername,
                  int score, int fullMarks, String examType, String remarks);

    /** All marks entered by a specific teacher, newest first. */
    List<Mark> getMarksByTeacher(String teacherUsername);

    /** All marks for a specific student entered by a specific teacher. */
    List<Mark> getMarksByStudentAndTeacher(String studentUsername, String teacherUsername);

    /** Delete a mark by id. */
    void deleteMark(long id);

    /** Distinct subject names used by a teacher. */
    List<String> getSubjectsByTeacher(String teacherUsername);

    /** All student usernames (STUDENT role). */
    List<String> getAllStudentUsernames();
}
