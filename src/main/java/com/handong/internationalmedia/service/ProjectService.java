package com.handong.internationalmedia.service;

import com.handong.internationalmedia.entity.Member;
import com.handong.internationalmedia.entity.Project;
import com.handong.internationalmedia.repository.MemberRepository;
import com.handong.internationalmedia.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByStartDateBetween(startDate, endDate);
    }

    public List<Project> getProjectsByMemberId(Long memberId) {
        return projectRepository.findByMembersId(memberId);
    }

    public List<Project> searchProjectsByTitle(String keyword) {
        return projectRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional
    public Optional<Project> updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setTitle(updatedProject.getTitle());
                    project.setDescription(updatedProject.getDescription());
                    project.setStatus(updatedProject.getStatus());
                    project.setStartDate(updatedProject.getStartDate());
                    project.setEndDate(updatedProject.getEndDate());
                    project.setProjectUrl(updatedProject.getProjectUrl());
                    project.setGithubUrl(updatedProject.getGithubUrl());
                    return projectRepository.save(project);
                });
    }

    @Transactional
    public Optional<Project> addMemberToProject(Long projectId, Long memberId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (projectOpt.isPresent() && memberOpt.isPresent()) {
            Project project = projectOpt.get();
            Member member = memberOpt.get();
            project.getMembers().add(member);
            return Optional.of(projectRepository.save(project));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Project> removeMemberFromProject(Long projectId, Long memberId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (projectOpt.isPresent() && memberOpt.isPresent()) {
            Project project = projectOpt.get();
            Member member = memberOpt.get();
            project.getMembers().remove(member);
            return Optional.of(projectRepository.save(project));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}