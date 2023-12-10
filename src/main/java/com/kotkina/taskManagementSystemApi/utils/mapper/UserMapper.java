package com.kotkina.taskManagementSystemApi.utils.mapper;

import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.web.models.responses.UserResponse;

public class UserMapper {

    public static UserResponse userToResponse(User user) {
        if (user == null) return null;

        UserResponse userResponse = new UserResponse();
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }
}
