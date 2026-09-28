package com.library.management.service;

import com.library.management.dto.StudentRequest;
import com.library.management.entity.Student;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public Student createStudent(StudentRequest request) {
        String email = request.getEmail().trim();
        if (studentRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Student with email '" + email + "' already exists.");
        }

        Student student = new Student();
        student.setName(request.getName().trim());
        student.setDepartment(request.getDepartment().trim());
        student.setYear(request.getYear().trim());
        student.setEmail(email);
        student.setPhone(request.getPhone() != null && !request.getPhone().trim().isEmpty() ? request.getPhone().trim() : null);

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentRequest request) {
        Student student = getStudentById(id);

        String newEmail = request.getEmail().trim();
        if (!student.getEmail().equalsIgnoreCase(newEmail)) {
            studentRepository.findByEmail(newEmail).ifPresent(existing -> {
                if (!existing.getStudentId().equals(id)) {
                    throw new DuplicateResourceException("Student with email '" + newEmail + "' already exists.");
                }
            });
        }

        student.setName(request.getName().trim());
        student.setDepartment(request.getDepartment().trim());
        student.setYear(request.getYear().trim());
        student.setEmail(newEmail);
        student.setPhone(request.getPhone() != null && !request.getPhone().trim().isEmpty() ? request.getPhone().trim() : null);

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(keyword.trim());
    }
}
