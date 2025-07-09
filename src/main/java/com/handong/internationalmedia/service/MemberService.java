package com.handong.internationalmedia.service;

import com.handong.internationalmedia.entity.Member;
import com.handong.internationalmedia.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public List<Member> getActiveMembers() {
        return memberRepository.findByIsActiveTrue();
    }

    public List<Member> getMembersByRole(Member.MemberRole role) {
        return memberRepository.findByRole(role);
    }

    public List<Member> getMembersByDepartment(String department) {
        return memberRepository.findByDepartment(department);
    }

    @Transactional
    public Optional<Member> updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id)
                .map(member -> {
                    member.setName(updatedMember.getName());
                    member.setEmail(updatedMember.getEmail());
                    member.setStudentId(updatedMember.getStudentId());
                    member.setDepartment(updatedMember.getDepartment());
                    member.setPhoneNumber(updatedMember.getPhoneNumber());
                    member.setRole(updatedMember.getRole());
                    member.setBio(updatedMember.getBio());
                    member.setIsActive(updatedMember.getIsActive());
                    return memberRepository.save(member);
                });
    }

    @Transactional
    public boolean deleteMember(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }
}