package com.example.nymmp.dto.user;

import com.example.nymmp.model.Group;
import com.example.nymmp.model.User;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequest {
    @Getter
    @Setter
    public static class JoinDTO {
        @NotBlank(message = "email is required.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Please enter a valid email address")
        private String email;

        @NotBlank(message = "password is required.")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "It must contain letters, numbers, and special characters, and cannot contain spaces")
        private String password;

        @NotBlank(message = "name is required.")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
        private String name;

        private String groupName;

        public User toEntity(String encodedPassword, Group group) {
            return User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .username(name)
                    .group(group)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class LoginDTO {
        @NotBlank(message = "email is required.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Please enter a valid email address.")
        private String email;

        @NotBlank(message = "password is required.")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "It must contain letters, numbers, and special characters, and cannot contain spaces.")
        private String password;
    }

    @Getter
    @Setter
    public static class EmailCheckDTO {
        @NotBlank(message = "email is required.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Please enter a valid email address")
        private String email;
    }

    @Getter
    @Setter
    public static class UpdatePasswordDTO {
        @NotBlank(message = "password is required.")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "It must contain letters, numbers, and special characters, and cannot contain spaces")
        private String password;
    }
}
