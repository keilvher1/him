package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.dto.StudentDto;
import com.handong.internationalmedia.entity.Student;
import com.handong.internationalmedia.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student API", description = "학생 관리 REST API")
public class StudentRestController {
    private final StudentService studentService;

    @Operation(summary = "학생 목록 조회", description = "전체 학생 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "검색할 이름") @RequestParam(required = false) String name,
            @Parameter(description = "검색할 이메일") @RequestParam(required = false) String email) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage;
        
        if ((name != null && !name.trim().isEmpty()) || (email != null && !email.trim().isEmpty())) {
            String keyword = name != null && !name.trim().isEmpty() ? name.trim() : email.trim();
            studentPage = studentService.searchByNameOrEmail(keyword, pageable);
        } else {
            studentPage = studentService.getAllStudents(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("students", studentPage.getContent());
        response.put("currentPage", studentPage.getNumber());
        response.put("totalItems", studentPage.getTotalElements());
        response.put("totalPages", studentPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "학생 상세 조회", description = "ID로 특정 학생을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "학생 ID") @PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "학생 등록", description = "새로운 학생을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<?> createStudent(
            @Parameter(description = "학생 정보") @Valid @RequestBody StudentDto studentDto) {
        try {
            Student createdStudent = studentService.createStudent(studentDto);
            return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "학생 정보 수정", description = "기존 학생 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @Parameter(description = "학생 ID") @PathVariable Long id,
            @Parameter(description = "수정할 학생 정보") @Valid @RequestBody StudentDto studentDto) {
        try {
            return studentService.updateStudent(id, studentDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "학생 삭제", description = "학생을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "학생 ID") @PathVariable Long id) {
        if (studentService.deleteStudent(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "이메일로 학생 조회", description = "이메일 주소로 학생을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(
            @Parameter(description = "학생 이메일") @PathVariable String email) {
        return studentService.getStudentByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}