package com.handong.internationalmedia.repository;

import com.handong.internationalmedia.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(Project.ProjectStatus status);
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Project> findByMembersId(Long memberId);
    List<Project> findByTitleContainingIgnoreCase(String keyword);
}