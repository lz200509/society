// ClubApplicationService.java
package com.example.society.service;

import com.example.society.model.ClubApplication;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClubApplicationService {

    // 获取所有申请记录
    List<ClubApplication> findAllApplications();

    // 根据ID查找申请记录
    Optional<ClubApplication> findApplicationById(Integer recordId);

    // 根据学生ID查找申请记录
    List<ClubApplication> findApplicationsByStudentId(Integer studentId);

    // 根据社团ID查找申请记录
    List<ClubApplication> findApplicationsByClubId(Integer clubId);

    // 根据审核状态查找申请记录
    List<ClubApplication> findApplicationsByAuditStatus(String auditStatus);

    // 根据学生ID和社团ID查找申请记录
    Optional<ClubApplication> findApplicationByStudentAndClub(Integer studentId, Integer clubId);

    // 根据学生ID和审核状态查找申请记录
    List<ClubApplication> findApplicationsByStudentAndStatus(Integer studentId, String auditStatus);

    // 根据社团ID和审核状态查找申请记录
    List<ClubApplication> findApplicationsByClubAndStatus(Integer clubId, String auditStatus);

    // 提交社团申请
    ClubApplication submitApplication(ClubApplication application);

    // 审核申请
    void auditApplication(Integer recordId, String auditStatus);

    // 删除申请记录
    void deleteApplication(Integer recordId);

    // 检查学生是否已经申请过该社团
    boolean existsByStudentAndClub(Integer studentId, Integer clubId);

    // 统计学生的申请数量
    long countApplicationsByStudent(Integer studentId);

    // 统计社团的申请数量
    long countApplicationsByClub(Integer clubId);

    // 根据时间范围查找申请记录
    List<ClubApplication> findApplicationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}