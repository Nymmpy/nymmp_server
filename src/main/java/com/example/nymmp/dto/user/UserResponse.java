package com.example.nymmp.dto.user;

import com.example.nymmp.model.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {
    @Getter
    @Setter
    public static class FindById {
        private long userId;
        private String username;
        private String email;

        public FindById(User user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String jwtToken;
        private String redirectUrl;

        public LoginResponse(String jwtToken, String redirectUrl) {
            this.jwtToken = jwtToken;
            this.redirectUrl = redirectUrl;
        }
    }

    @Setter
    @Getter
    public static class MyPage{
        private long userId;
        private String username;
        private long groupId;
        private String groupName;

        public MyPage(User user){
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.groupId = user.getGroup().getGroupId();
            this.groupName = user.getGroup().getGroupName();
        }
    }
}
