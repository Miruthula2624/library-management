package com.library.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StudentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Year is required")
    private String year;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String phone;

    public StudentRequest() {
    }

    public StudentRequest(String name, String department, String year, String email, String phone) {
        this.name = name;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
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
