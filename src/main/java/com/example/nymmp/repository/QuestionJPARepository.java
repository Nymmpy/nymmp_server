package com.example.nymmp.repository;

import com.example.nymmp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJPARepository extends JpaRepository<Question, Long> {
}
