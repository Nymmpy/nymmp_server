package com.example.nymmp.controller;

import com.example.nymmp.dto.poll.*;
import com.example.nymmp.service.PollService;
import com.example.nymmp._core.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poll")
public class PollController {

    private final PollService pollService;

    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    public ResponseEntity<?> getPoll(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        Long groupId = JwtUtil.getGroupIdFromToken(token);
        Long pollId = pollService.getNextPollId(userId, groupId);

        PollResponse pollResponse = pollService.getPollById(pollId, groupId);
        return ResponseEntity.ok(pollResponse);
    }

    @GetMapping("/{pollId}")
    public ResponseEntity<?> getPollById(@PathVariable Long pollId, @RequestHeader("Authorization") String token) {
        Long groupId = JwtUtil.getGroupIdFromToken(token);

        PollResponse pollResponse = pollService.getPollById(pollId, groupId);
        return ResponseEntity.ok(pollResponse);
    }

    @GetMapping("/{pollId}/shuffle")
    public ResponseEntity<?> shuffleOptions(@PathVariable Long pollId, @RequestHeader("Authorization") String token) {
        Long groupId = JwtUtil.getGroupIdFromToken(token);

        List<PollOptionResponse> options = pollService.shuffleOptions(pollId, groupId);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/vote")
    public ResponseEntity<?> submitVote(@RequestBody PollVoteRequest voteRequest, @RequestHeader("Authorization") String token) {
        Long groupId = JwtUtil.getGroupIdFromToken(token);

        VoteResponse voteResponse = pollService.submitVote(voteRequest, groupId);
        return ResponseEntity.ok(voteResponse);
    }
}
