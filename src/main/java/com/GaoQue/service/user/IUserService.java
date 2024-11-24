package com.GaoQue.service.user;

import com.GaoQue.dto.UserDto;
import com.GaoQue.model.User;
import com.GaoQue.request.CreateUserRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    UserDto convertUserToDto(User user);
}
