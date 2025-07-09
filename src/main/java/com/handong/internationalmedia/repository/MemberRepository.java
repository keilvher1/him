package com.handong.internationalmedia.repository;

import com.handong.internationalmedia.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByIsActiveTrue();
    List<Member> findByRole(Member.MemberRole role);
    List<Member> findByDepartment(String department);
}