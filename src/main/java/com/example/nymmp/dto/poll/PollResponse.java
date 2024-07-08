package com.example.nymmp.dto.poll;

import java.util.List;

public class PollResponse {
    private Long pollId;
    private String question;
    private int totalCount;
    private List<PollOptionResponse> options;
    private List<TopVoterResponse> topVoters;

    public PollResponse(Long pollId, String question, int totalCount, List<PollOptionResponse> options, List<TopVoterResponse> topVoters) {
        this.pollId = pollId;
        this.question = question;
        this.totalCount = totalCount;
        this.options = options;
        this.topVoters = topVoters;
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

    public List<TopVoterResponse> getTopVoters() {
        return topVoters;
    }

    public void setTopVoters(List<TopVoterResponse> topVoters) {
        this.topVoters = topVoters;
    }
}
