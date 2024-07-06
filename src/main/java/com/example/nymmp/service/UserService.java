package com.example.nymmp.service;

import com.example.nymmp.model.User;
import com.example.nymmp.repository.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserJPARepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
