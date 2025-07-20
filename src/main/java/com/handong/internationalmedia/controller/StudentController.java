package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.dto.StudentDto;
import com.handong.internationalmedia.entity.Student;
import com.handong.internationalmedia.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public String listStudents(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String email,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage;
        
        if ((name != null && !name.trim().isEmpty()) || (email != null && !email.trim().isEmpty())) {
            String keyword = name != null && !name.trim().isEmpty() ? name.trim() : email.trim();
            studentPage = studentService.searchByNameOrEmail(keyword, pageable);
            model.addAttribute("searchName", name);
            model.addAttribute("searchEmail", email);
        } else {
            studentPage = studentService.getAllStudents(pageable);
        }
        
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("pageTitle", "학생 목록");
        
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDto());
        model.addAttribute("pageTitle", "새 학생 등록");
        return "students/form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("student") StudentDto studentDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "새 학생 등록");
            return "students/form";
        }
        
        try {
            studentService.createStudent(studentDto);
            redirectAttributes.addFlashAttribute("successMessage", "학생이 성공적으로 등록되었습니다.");
            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "학생 등록 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("pageTitle", "새 학생 등록");
            return "students/form";
        }
    }

    @GetMapping("/{id}")
    public String showStudent(@PathVariable Long id, Model model) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    model.addAttribute("pageTitle", student.getName() + " - 학생 정보");
                    return "students/detail";
                })
                .orElse("redirect:/students");
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return studentService.getStudentById(id)
                .map(student -> {
                    StudentDto studentDto = new StudentDto();
                    studentDto.setId(student.getId());
                    studentDto.setName(student.getName());
                    studentDto.setEmail(student.getEmail());
                    
                    model.addAttribute("student", studentDto);
                    model.addAttribute("pageTitle", "학생 정보 수정");
                    return "students/form";
                })
                .orElse("redirect:/students");
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id,
                               @Valid @ModelAttribute("student") StudentDto studentDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "학생 정보 수정");
            return "students/form";
        }
        
        try {
            studentService.updateStudent(id, studentDto);
            redirectAttributes.addFlashAttribute("successMessage", "학생 정보가 성공적으로 수정되었습니다.");
            return "redirect:/students/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "학생 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("pageTitle", "학생 정보 수정");
            return "students/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (studentService.deleteStudent(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "학생이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "학생 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/students";
    }
}