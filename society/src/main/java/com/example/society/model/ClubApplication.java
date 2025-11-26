package com.example.society.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_applications", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "club_id"}))
public class ClubApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Club club;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @Column(name = "audit_status", nullable = false, length = 20)
    private String auditStatus;

    @PrePersist
    public void prePersist() {
        if (applyTime == null) {
            applyTime = LocalDateTime.now();
        }
        if (auditStatus == null) {
            auditStatus = "待审核";
        }
    }
    // 构造函数
    public ClubApplication() {
    }

    public ClubApplication(Student student, Club club) {
        this.student = student;
        this.club = club;
        this.auditStatus = "待审核";
    }
    // Getter和Setter方法
    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    @Override
    public String toString() {
        return "ClubApplication{" +
                "recordId=" + recordId +
                ", student=" + (student != null ? student.getStudentName() : "null") +
                ", club=" + (club != null ? club.getClubName() : "null") +
                ", applyTime=" + applyTime +
                ", auditStatus='" + auditStatus + '\'' +
                '}';
    }
}
