package com.handong.internationalmedia.repository;

import com.handong.internationalmedia.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Page<Student> findByNameContaining(String name, Pageable pageable);
    Page<Student> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);
    boolean existsByEmail(String email);
}