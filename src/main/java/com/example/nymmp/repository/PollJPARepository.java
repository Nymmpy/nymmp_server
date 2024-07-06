package com.example.nymmp.repository;

import com.example.nymmp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollJPARepository extends JpaRepository<Poll, Long> {
}
