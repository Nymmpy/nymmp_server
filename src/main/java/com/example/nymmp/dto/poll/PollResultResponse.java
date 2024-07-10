package com.example.nymmp.dto.poll;

import java.util.List;

public class PollResultResponse {
    private PollResponse pollResponse;
    private List<TopVoterResponse> topVoters;

    public PollResultResponse(PollResponse pollResponse, List<TopVoterResponse> topVoters) {
        this.pollResponse = pollResponse;
        this.topVoters = topVoters;
    }

    public PollResponse getPollResponse() {
        return pollResponse;
    }

    public void setPollResponse(PollResponse pollResponse) {
        this.pollResponse = pollResponse;
    }

    public List<TopVoterResponse> getTopVoters() {
        return topVoters;
    }

    public void setTopVoters(List<TopVoterResponse> topVoters) {
        this.topVoters = topVoters;
    }
}
