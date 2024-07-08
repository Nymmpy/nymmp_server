package com.example.nymmp.dto.poll;

public class PollOptionResponse {
    private Long userId;
    private String username;
    private int voteCount;

    public PollOptionResponse(Long userId, String username, int voteCount) {
        this.userId = userId;
        this.username = username;
        this.voteCount = voteCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
