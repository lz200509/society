package com.example.society.repository;

import com.example.society.model.Club;
import com.example.society.model.ClubApplication;
import com.example.society.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Integer> {

    // 根据学生查找申请记录
    List<ClubApplication> findByStudent(Student student);

    // 根据社团查找申请记录
    List<ClubApplication> findByClub(Club club);

    // 根据审核状态查找申请记录
    List<ClubApplication> findByAuditStatus(String auditStatus);

    // 根据学生和社团查找申请记录
    Optional<ClubApplication> findByStudentAndClub(Student student, Club club);

    // 根据学生ID和社团ID查找申请记录
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.student.studentId = :studentId AND ca.club.clubId = :clubId")
    Optional<ClubApplication> findByStudentIdAndClubId(@Param("studentId") Integer studentId, @Param("clubId") Integer clubId);

    // 根据学生ID查找申请记录
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.student.studentId = :studentId")
    List<ClubApplication> findByStudentId(@Param("studentId") Integer studentId);

    // 根据社团ID查找申请记录
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.club.clubId = :clubId")
    List<ClubApplication> findByClubId(@Param("clubId") Integer clubId);

    // 根据学生ID和审核状态查找申请记录
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.student.studentId = :studentId AND ca.auditStatus = :auditStatus")
    List<ClubApplication> findByStudentIdAndAuditStatus(@Param("studentId") Integer studentId, @Param("auditStatus") String auditStatus);

    // 根据社团ID和审核状态查找申请记录
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.club.clubId = :clubId AND ca.auditStatus = :auditStatus")
    List<ClubApplication> findByClubIdAndAuditStatus(@Param("clubId") Integer clubId, @Param("auditStatus") String auditStatus);

    // 更新申请状态
    @Modifying
    @Query("UPDATE ClubApplication ca SET ca.auditStatus = :auditStatus WHERE ca.recordId = :recordId")
    void updateAuditStatus(@Param("recordId") Integer recordId, @Param("auditStatus") String auditStatus);

    // 检查学生是否已经申请过该社团
    boolean existsByStudentAndClub(Student student, Club club);

    // 统计某个学生的申请数量
    long countByStudent(Student student);

    // 统计某个社团的申请数量
    long countByClub(Club club);

    // 根据申请时间范围查找
    @Query("SELECT ca FROM ClubApplication ca WHERE ca.applyTime BETWEEN :startTime AND :endTime")
    List<ClubApplication> findByApplyTimeBetween(@Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
}