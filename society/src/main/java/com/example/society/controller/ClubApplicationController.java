// ClubApplicationController.java
package com.example.society.controller;

import com.example.society.dto.ClubApplicationRequest;
import com.example.society.model.Club;
import com.example.society.model.ClubApplication;
import com.example.society.model.Student;
import com.example.society.service.ClubApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
public class ClubApplicationController {

    @Autowired
    private ClubApplicationService clubApplicationService;

    // 获取所有申请记录
    @GetMapping
    public ResponseEntity<List<ClubApplication>> getAllApplications() {
        try {
            List<ClubApplication> applications = clubApplicationService.findAllApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据ID获取申请记录
    @GetMapping("/{recordId}")
    public ResponseEntity<ClubApplication> getApplicationById(@PathVariable Integer recordId) {
        try {
            Optional<ClubApplication> application = clubApplicationService.findApplicationById(recordId);
            return application.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据学生ID获取申请记录
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClubApplication>> getApplicationsByStudentId(@PathVariable Integer studentId) {
        try {
            List<ClubApplication> applications = clubApplicationService.findApplicationsByStudentId(studentId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据社团ID获取申请记录
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ClubApplication>> getApplicationsByClubId(@PathVariable Integer clubId) {
        try {
            List<ClubApplication> applications = clubApplicationService.findApplicationsByClubId(clubId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据审核状态获取申请记录
    @GetMapping("/status/{auditStatus}")
    public ResponseEntity<List<ClubApplication>> getApplicationsByStatus(@PathVariable String auditStatus) {
        try {
            List<ClubApplication> applications = clubApplicationService.findApplicationsByAuditStatus(auditStatus);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据学生ID和社团ID获取申请记录
    @GetMapping("/student/{studentId}/club/{clubId}")
    public ResponseEntity<ClubApplication> getApplicationByStudentAndClub(
            @PathVariable Integer studentId, @PathVariable Integer clubId) {
        try {
            Optional<ClubApplication> application =
                    clubApplicationService.findApplicationByStudentAndClub(studentId, clubId);
            return application.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据学生ID和状态获取申请记录
    @GetMapping("/student/{studentId}/status/{auditStatus}")
    public ResponseEntity<List<ClubApplication>> getApplicationsByStudentAndStatus(
            @PathVariable Integer studentId, @PathVariable String auditStatus) {
        try {
            List<ClubApplication> applications =
                    clubApplicationService.findApplicationsByStudentAndStatus(studentId, auditStatus);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据社团ID和状态获取申请记录
    @GetMapping("/club/{clubId}/status/{auditStatus}")
    public ResponseEntity<List<ClubApplication>> getApplicationsByClubAndStatus(
            @PathVariable Integer clubId, @PathVariable String auditStatus) {
        try {
            List<ClubApplication> applications =
                    clubApplicationService.findApplicationsByClubAndStatus(clubId, auditStatus);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 提交社团申请
    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody ClubApplicationRequest request) {
        try {
            // 创建新的申请对象
            ClubApplication application = new ClubApplication();

            // 设置学生和社团
            Student student = new Student();
            student.setStudentId(request.getStudentId());

            Club club = new Club();
            club.setClubId(request.getClubId());

            application.setStudent(student);
            application.setClub(club);
            application.setAuditStatus(request.getAuditStatus() != null ? request.getAuditStatus() : "待审核");

            ClubApplication savedApplication = clubApplicationService.submitApplication(application);
            return ResponseEntity.ok(savedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("提交申请失败: " + e.getMessage());
        }
    }

    // 审核申请
    @PutMapping("/{recordId}/audit")
    public ResponseEntity<?> auditApplication(
            @PathVariable Integer recordId, @RequestParam String auditStatus) {
        try {
            clubApplicationService.auditApplication(recordId, auditStatus);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("审核失败: " + e.getMessage());
        }
    }

    // 删除申请记录
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Integer recordId) {
        try {
            clubApplicationService.deleteApplication(recordId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除申请记录失败: " + e.getMessage());
        }
    }

    // 检查学生是否已经申请过该社团
    @GetMapping("/check-application")
    public ResponseEntity<Boolean> checkApplicationExists(
            @RequestParam Integer studentId, @RequestParam Integer clubId) {
        try {
            boolean exists = clubApplicationService.existsByStudentAndClub(studentId, clubId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 统计学生的申请数量
    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<Long> countApplicationsByStudent(@PathVariable Integer studentId) {
        try {
            long count = clubApplicationService.countApplicationsByStudent(studentId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 统计社团的申请数量
    @GetMapping("/club/{clubId}/count")
    public ResponseEntity<Long> countApplicationsByClub(@PathVariable Integer clubId) {
        try {
            long count = clubApplicationService.countApplicationsByClub(clubId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据时间范围查找申请记录
    @GetMapping("/time-range")
    public ResponseEntity<List<ClubApplication>> getApplicationsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<ClubApplication> applications =
                    clubApplicationService.findApplicationsByTimeRange(startTime, endTime);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}