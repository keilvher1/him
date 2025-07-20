package com.handong.internationalmedia.service;

import com.handong.internationalmedia.dto.StudentDto;
import com.handong.internationalmedia.entity.Student;
import com.handong.internationalmedia.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public Student createStudent(StudentDto studentDto) {
        if (studentRepository.existsByEmail(studentDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        Student student = Student.builder()
                .name(studentDto.getName())
                .email(studentDto.getEmail())
                .build();
        
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }
    
    public Page<Student> searchByName(String name, Pageable pageable) {
        return studentRepository.findByNameContaining(name, pageable);
    }
    
    public Page<Student> searchByNameOrEmail(String keyword, Pageable pageable) {
        return studentRepository.findByNameContainingOrEmailContaining(keyword, keyword, pageable);
    }

    @Transactional
    public Optional<Student> updateStudent(Long id, StudentDto studentDto) {
        return studentRepository.findById(id)
                .map(student -> {
                    // Check if email is being changed and if new email already exists
                    if (!student.getEmail().equals(studentDto.getEmail()) && 
                        studentRepository.existsByEmail(studentDto.getEmail())) {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                    }
                    
                    student.setName(studentDto.getName());
                    student.setEmail(studentDto.getEmail());
                    return studentRepository.save(student);
                });
    }

    @Transactional
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}