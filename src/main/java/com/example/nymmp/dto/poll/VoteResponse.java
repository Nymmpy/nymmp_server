package com.example.nymmp.dto.poll;

public class VoteResponse {
    private boolean success;
    private String message;
    private Long nextPollId;

    public VoteResponse(boolean success, String message, Long nextPollId) {
        this.success = success;
        this.message = message;
        this.nextPollId = nextPollId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getNextPollId() {
        return nextPollId;
    }

    public void setNextPollId(Long nextPollId) {
        this.nextPollId = nextPollId;
    }
}
