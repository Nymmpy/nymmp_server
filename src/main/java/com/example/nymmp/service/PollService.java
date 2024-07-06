package com.example.nymmp.service;

import com.example.nymmp.model.Poll;
import com.example.nymmp.repository.PollJPARepository;
import com.example.nymmp.repository.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PollService {
    @Autowired
    private PollJPARepository pollRepository;

    @Autowired
    private UserJPARepository userRepository;

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }
}