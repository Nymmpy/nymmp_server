package com.example.nymmp.dto.poll;

public class PollVoteRequest {
    private Long userId;
    private Long pollId;
    private Long selected;
    private Boolean isLast;

    public PollVoteRequest() {
    }

    public PollVoteRequest(Long userId, Long pollId, Long selected, Boolean isLast) {
        this.userId = userId;
        this.pollId = pollId;
        this.selected = selected;
        this.isLast = isLast;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Long getSelected() {
        return selected;
    }

    public void setSelected(Long selected) {
        this.selected = selected;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public void setIsLast(Boolean isLast) {
        this.isLast = isLast;
    }
}
