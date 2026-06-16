package com.hammroschool.service;

import java.util.List;

import com.hammroschool.model.auth.UserAccount;

public interface TeacherService {
    List<UserAccount> getAllTeachers();
}
