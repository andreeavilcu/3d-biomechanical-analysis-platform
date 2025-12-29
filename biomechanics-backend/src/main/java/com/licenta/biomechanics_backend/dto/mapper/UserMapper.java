package com.licenta.biomechanics_backend.dto.mapper;

import com.licenta.biomechanics_backend.dto.request.CreateUserRequest;
import com.licenta.biomechanics_backend.dto.response.UserResponse;
import com.licenta.biomechanics_backend.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request){
        User user = new User();
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        return user;
    }

    public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .age(calculateAge(user.getDateOfBirth()))
                .gender(user.getGender())
                .email(user.getEmail())
                .totalAssessments(user.getAssessments() != null ? user.getAssessments().size() : 0)
                .build();
    }

    private Integer calculateAge(LocalDate dateOfBirth){
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
