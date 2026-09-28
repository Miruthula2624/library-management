package com.library.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Department cannot be empty")
    @Column(name = "department", nullable = false)
    private String department;

    @NotBlank(message = "Year cannot be empty")
    @Column(name = "year", nullable = false)
    private String year;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    public Student() {
    }

    public Student(Long studentId, String name, String department, String year, String email, String phone) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
    }

    public Student(String name, String department, String year, String email, String phone) {
        this.name = name;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
