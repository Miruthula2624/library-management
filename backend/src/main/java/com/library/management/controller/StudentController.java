package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.StudentRequest;
import com.library.management.entity.Student;
import com.library.management.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam(required = false) String keyword) {
        List<Student> students = studentService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentRequest request) {
        Student created = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        Student updated = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
