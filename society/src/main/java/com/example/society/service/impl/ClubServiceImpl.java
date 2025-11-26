// ClubServiceImpl.java
package com.example.society.service.impl;

import com.example.society.model.Club;
import com.example.society.repository.ClubRepository;
import com.example.society.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClubServiceImpl implements ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Club> findAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Club> findClubById(Integer clubId) {
        return clubRepository.findById(clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Club> findClubByName(String clubName) {
        return clubRepository.findByClubName(clubName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Club> findClubsByType(String clubType) {
        return clubRepository.findByClubType(clubType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Club> searchClubsByName(String clubName) {
        return clubRepository.findByClubNameContaining(clubName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Club> findClubsWithRemainingQuotaGreaterThan(Integer quota) {
        return clubRepository.findByRemainingQuotaGreaterThan(quota);
    }

    @Override
    public Club createClub(Club club) {
        // 设置初始剩余名额等于总名额
        if (club.getRemainingQuota() == null) {
            club.setRemainingQuota(club.getTotalQuota());
        }
        return clubRepository.save(club);
    }

    @Override
    public Club updateClub(Club club) {
        if (!clubRepository.existsById(club.getClubId())) {
            throw new RuntimeException("社团不存在，ID: " + club.getClubId());
        }
        return clubRepository.save(club);
    }

    @Override
    public void updateRemainingQuota(Integer clubId, Integer remainingQuota) {
        clubRepository.updateRemainingQuota(clubId, remainingQuota);
    }

    @Override
    public void deleteClub(Integer clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new RuntimeException("社团不存在，ID: " + clubId);
        }
        clubRepository.deleteById(clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClubName(String clubName) {
        return clubRepository.existsByClubName(clubName);
    }
}