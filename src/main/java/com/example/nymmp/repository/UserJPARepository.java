package com.example.nymmp.repository;

import com.example.nymmp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface UserJPARepository extends JpaRepository<User, Long> {

}

