package com.example.nymmp.repository;

import com.example.nymmp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollJPARepository extends JpaRepository<Poll, Long> {
    List<Poll> findByGroup_GroupId(Long groupId);

}
