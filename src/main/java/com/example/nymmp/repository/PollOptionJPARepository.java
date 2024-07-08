package com.example.nymmp.repository;

import com.example.nymmp.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollOptionJPARepository extends JpaRepository<PollOption, Long> {

//    @Query("SELECT po FROM PollOption po JOIN FETCH po.poll p WHERE p.pollId = :pollId")
    Optional<PollOption> findByPoll_PollId(Long pollId);
}
