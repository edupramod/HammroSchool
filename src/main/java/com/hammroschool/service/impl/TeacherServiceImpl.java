package com.hammroschool.service.impl;

import com.hammroschool.model.auth.UserAccount;
import com.hammroschool.model.auth.UserRole;
import com.hammroschool.service.TeacherService;

import java.util.List;

public class TeacherServiceImpl implements TeacherService {

    private static final TeacherServiceImpl INSTANCE = new TeacherServiceImpl();
    private final InMemoryAuthService authService = InMemoryAuthService.getInstance();

    private TeacherServiceImpl() {}

    public static TeacherServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<UserAccount> getAllTeachers() {
        return authService.getAccounts().stream()
            .filter(account -> account.getRole() == UserRole.TEACHER)
            .toList();
    }
}
