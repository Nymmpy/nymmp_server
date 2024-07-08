package com.example.nymmp.repository;

import com.example.nymmp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Repository
public interface UserJPARepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u JOIN FETCH u.group WHERE u.group.groupId = :groupId")
    List<User> findByGroupId(Long groupId);
}

