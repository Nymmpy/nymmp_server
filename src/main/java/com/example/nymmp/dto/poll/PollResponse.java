package com.example.nymmp.dto.poll;

import java.util.List;

public class PollResponse {
    private Long pollId;
    private Long questionId;
    private String question;
    private int totalCount;
    private List<PollOptionResponse> options;

    public PollResponse(Long pollId, Long questionId,String question, int totalCount, List<PollOptionResponse> options) {
        this.pollId = pollId;
        this.questionId = questionId;
        this.question = question;
        this.totalCount = totalCount;
        this.options = options;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<PollOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionResponse> options) {
        this.options = options;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public PollResponse setQuestionId(Long questionId) {
        this.questionId = questionId;
        return this;
    }
}
