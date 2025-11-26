// ClubApplicationServiceImpl.java
package com.example.society.service.impl;

import com.example.society.model.Club;
import com.example.society.model.ClubApplication;
import com.example.society.model.Student;
import com.example.society.repository.ClubApplicationRepository;
import com.example.society.repository.ClubRepository;
import com.example.society.repository.StudentRepository;
import com.example.society.service.ClubApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClubApplicationServiceImpl implements ClubApplicationService {

    @Autowired
    private ClubApplicationRepository clubApplicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findAllApplications() {
        return clubApplicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubApplication> findApplicationById(Integer recordId) {
        return clubApplicationRepository.findById(recordId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByStudentId(Integer studentId) {
        return clubApplicationRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByClubId(Integer clubId) {
        return clubApplicationRepository.findByClubId(clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByAuditStatus(String auditStatus) {
        return clubApplicationRepository.findByAuditStatus(auditStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubApplication> findApplicationByStudentAndClub(Integer studentId, Integer clubId) {
        return clubApplicationRepository.findByStudentIdAndClubId(studentId, clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByStudentAndStatus(Integer studentId, String auditStatus) {
        return clubApplicationRepository.findByStudentIdAndAuditStatus(studentId, auditStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByClubAndStatus(Integer clubId, String auditStatus) {
        return clubApplicationRepository.findByClubIdAndAuditStatus(clubId, auditStatus);
    }

    @Override
    public ClubApplication submitApplication(ClubApplication application) {
        // 验证必要字段
        if (application.getStudent() == null || application.getStudent().getStudentId() == null) {
            throw new RuntimeException("学生信息不能为空");
        }
        if (application.getClub() == null || application.getClub().getClubId() == null) {
            throw new RuntimeException("社团信息不能为空");
        }

        // 检查学生和社团是否存在
        Student student = studentRepository.findById(application.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + application.getStudent().getStudentId()));

        Club club = clubRepository.findById(application.getClub().getClubId())
                .orElseThrow(() -> new RuntimeException("社团不存在，ID: " + application.getClub().getClubId()));

        // 检查是否已经申请过
        if (clubApplicationRepository.existsByStudentAndClub(student, club)) {
            throw new RuntimeException("您已经申请过该社团");
        }

        // 检查社团是否还有剩余名额
        if (club.getRemainingQuota() <= 0) {
            throw new RuntimeException("该社团已满员，无法申请");
        }

        // 设置关联对象
        application.setStudent(student);
        application.setClub(club);

        // 确保申请时间不为空
        if (application.getApplyTime() == null) {
            application.setApplyTime(LocalDateTime.now());
        }

        // 确保审核状态不为空
        if (application.getAuditStatus() == null) {
            application.setAuditStatus("待审核");
        }

        return clubApplicationRepository.save(application);
    }

    @Override
    public void auditApplication(Integer recordId, String auditStatus) {
        ClubApplication application = clubApplicationRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("申请记录不存在，ID: " + recordId));

        // 如果是审核通过，需要减少社团剩余名额
        if ("approved".equals(auditStatus) && !"approved".equals(application.getAuditStatus())) {
            Club club = application.getClub();
            if (club.getRemainingQuota() > 0) {
                club.setRemainingQuota(club.getRemainingQuota() - 1);
                clubRepository.save(club);
            } else {
                throw new RuntimeException("社团名额已满，无法通过申请");
            }
        }

        // 如果是撤销批准，需要恢复社团剩余名额
        if (!"approved".equals(auditStatus) && "approved".equals(application.getAuditStatus())) {
            Club club = application.getClub();
            club.setRemainingQuota(club.getRemainingQuota() + 1);
            clubRepository.save(club);
        }

        clubApplicationRepository.updateAuditStatus(recordId, auditStatus);
    }

    @Override
    public void deleteApplication(Integer recordId) {
        if (!clubApplicationRepository.existsById(recordId)) {
            throw new RuntimeException("申请记录不存在，ID: " + recordId);
        }
        clubApplicationRepository.deleteById(recordId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentAndClub(Integer studentId, Integer clubId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + studentId));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("社团不存在，ID: " + clubId));

        return clubApplicationRepository.existsByStudentAndClub(student, club);
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsByStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + studentId));
        return clubApplicationRepository.countByStudent(student);
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsByClub(Integer clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("社团不存在，ID: " + clubId));
        return clubApplicationRepository.countByClub(club);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubApplication> findApplicationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return clubApplicationRepository.findByApplyTimeBetween(startTime, endTime);
    }
}