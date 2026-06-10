package com.study.management.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private LocalDate createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = LocalDate.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPhone()               { return phone; }
    public void setPhone(String phone)     { this.phone = phone; }

    public LocalDate getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDate createdAt)    { this.createdAt = createdAt; }
}
