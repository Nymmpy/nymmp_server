package com.example.nymmp.repository;

import com.example.nymmp.model.PollResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollResultJPARepository extends JpaRepository<PollResult, Long> {

    @Query("SELECT pr FROM PollResult pr JOIN pr.poll p JOIN p.group g WHERE g.groupId = :groupId AND p.question.questionId = :questionId")
    List<PollResult> findByGroupIdAndQuestionId(Long groupId, Long questionId);

    @Query("SELECT pr FROM PollResult pr JOIN pr.poll p JOIN p.group g WHERE g.groupId = :groupId AND pr.choice.userId = :userId")
    List<PollResult> findByGroupIdAndUserId(Long groupId, Long userId);
}