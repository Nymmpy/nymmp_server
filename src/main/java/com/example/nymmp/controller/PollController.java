package com.example.nymmp.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.nymmp.dto.poll.*;
import com.example.nymmp.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poll")
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping
    public ResponseEntity<?> getPoll(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        Long groupId = getGroupIdFromToken(token);
        Long pollId = pollService.getNextPollId(userId, groupId);

        PollResponse pollResponse = pollService.getPollById(pollId, groupId);
        return ResponseEntity.ok(pollResponse);
    }

    @GetMapping("/{pollId}")
    public ResponseEntity<?> getPollById(@PathVariable Long pollId, @RequestHeader("Authorization") String token) {
        Long groupId = getGroupIdFromToken(token);

        PollResponse pollResponse = pollService.getPollById(pollId, groupId);
        return ResponseEntity.ok(pollResponse);
    }

    @GetMapping("/{pollId}/shuffle")
    public ResponseEntity<?> shuffleOptions(@PathVariable Long pollId, @RequestHeader("Authorization") String token) {
        Long groupId = getGroupIdFromToken(token);

        List<PollOptionResponse> options = pollService.shuffleOptions(pollId, groupId);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/vote")
    public ResponseEntity<?> submitVote(@RequestBody PollVoteRequest voteRequest) {
        VoteResponse voteResponse = pollService.submitVote(voteRequest);
        return ResponseEntity.ok(voteResponse);
    }

    private Long getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("MySecretKey")).build().verify(token.replace("Bearer ", ""));
            return decodedJWT.getClaim("id").asLong();
        } catch (SignatureVerificationException | TokenExpiredException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    private Long getGroupIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("MySecretKey")).build().verify(token.replace("Bearer ", ""));
            return decodedJWT.getClaim("group").asLong();
        } catch (SignatureVerificationException | TokenExpiredException e) {
            throw new RuntimeException("Invalid token");
        }
    }
}
