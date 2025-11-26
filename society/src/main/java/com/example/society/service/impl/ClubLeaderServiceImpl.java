// ClubLeaderServiceImpl.java
package com.example.society.service.impl;

import com.example.society.model.ClubLeader;
import com.example.society.repository.ClubLeaderRepository;
import com.example.society.service.ClubLeaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ClubLeaderServiceImpl implements ClubLeaderService {

    @Autowired
    private ClubLeaderRepository clubLeaderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClubLeader> findAllLeaders() {
        return clubLeaderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubLeader> findLeaderById(Integer leaderId) {
        return clubLeaderRepository.findById(leaderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubLeader> findLeaderByLeaderNum(String leaderNum) {
        return clubLeaderRepository.findByLeaderNum(leaderNum);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubLeader> findLeaderByClubId(Integer clubId) {
        return clubLeaderRepository.findByClubId(clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubLeader> findLeadersByName(String leaderName) {
        return clubLeaderRepository.findByLeaderName(leaderName);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubLeader> findLeaderByPhone(String phone) {
        return clubLeaderRepository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubLeader> login(String leaderNum, String password) {
        try {
            return clubLeaderRepository.findByLeaderNumAndPassword(leaderNum, password);
        } catch (Exception e) {
            log.error("负责人登录服务异常: {}", e.getMessage(), e);
            throw new RuntimeException("登录服务异常");
        }
    }

    @Override
    public ClubLeader createLeader(ClubLeader leader) {
        // 检查负责人编号是否已存在
        if (clubLeaderRepository.existsByLeaderNum(leader.getLeaderNum())) {
            throw new RuntimeException("负责人编号已存在: " + leader.getLeaderNum());
        }

        return clubLeaderRepository.save(leader);
    }

    @Override
    public ClubLeader updateLeader(ClubLeader leader) {
        if (!clubLeaderRepository.existsById(leader.getLeaderId())) {
            throw new RuntimeException("负责人不存在，ID: " + leader.getLeaderId());
        }
        return clubLeaderRepository.save(leader);
    }

    @Override
    public void deleteLeader(Integer leaderId) {
        if (!clubLeaderRepository.existsById(leaderId)) {
            throw new RuntimeException("负责人不存在，ID: " + leaderId);
        }
        clubLeaderRepository.deleteById(leaderId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLeaderNum(String leaderNum) {
        return clubLeaderRepository.existsByLeaderNum(leaderNum);
    }

}


