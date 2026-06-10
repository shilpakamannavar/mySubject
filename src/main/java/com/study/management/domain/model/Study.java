package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root — owns the visit collection and the invoice.
 * JPA annotations are kept here (pragmatic hexagonal).
 */
@Entity
@Table(name = "study")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String studyId;

    @NotBlank
    private String indication;

    @NotNull
    @Min(1)
    private Integer numberOfPatients;

    private LocalDate firstPatientScreeningDate;
    private LocalDate lastPatientVisitDate;

    @NotNull
    @Min(1)
    private Integer totalVisitsPerPatient;

    private String principalInvestigatorName;

    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("study-visits")
    private List<StudyVisit> visits = new ArrayList<>();

    @OneToOne(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("study-invoice")
    private Invoice invoice;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getStudyId()                   { return studyId; }
    public void setStudyId(String studyId)       { this.studyId = studyId; }

    public String getIndication()                { return indication; }
    public void setIndication(String indication) { this.indication = indication; }

    public Integer getNumberOfPatients()                        { return numberOfPatients; }
    public void setNumberOfPatients(Integer numberOfPatients)   { this.numberOfPatients = numberOfPatients; }

    public LocalDate getFirstPatientScreeningDate()                              { return firstPatientScreeningDate; }
    public void setFirstPatientScreeningDate(LocalDate firstPatientScreeningDate){ this.firstPatientScreeningDate = firstPatientScreeningDate; }

    public LocalDate getLastPatientVisitDate()                          { return lastPatientVisitDate; }
    public void setLastPatientVisitDate(LocalDate lastPatientVisitDate) { this.lastPatientVisitDate = lastPatientVisitDate; }

    public Integer getTotalVisitsPerPatient()                          { return totalVisitsPerPatient; }
    public void setTotalVisitsPerPatient(Integer totalVisitsPerPatient){ this.totalVisitsPerPatient = totalVisitsPerPatient; }

    public String getPrincipalInvestigatorName()                    { return principalInvestigatorName; }
    public void setPrincipalInvestigatorName(String v)             { this.principalInvestigatorName = v; }

    public StudyStatus getStatus()               { return status; }
    public void setStatus(StudyStatus status)    { this.status = status; }

    public List<StudyVisit> getVisits()          { return visits; }
    public void setVisits(List<StudyVisit> v)    { this.visits = v; }

    public Invoice getInvoice()                  { return invoice; }
    public void setInvoice(Invoice invoice)      { this.invoice = invoice; }

    public Customer getCustomer()                { return customer; }
    public void setCustomer(Customer customer)   { this.customer = customer; }
}