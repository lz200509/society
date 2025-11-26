package com.example.society.repository;

import com.example.society.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {

    // 根据社团名称查找社团
    Optional<Club> findByClubName(String clubName);

    // 根据社团类型查找社团
    List<Club> findByClubType(String clubType);

    // 查找剩余名额大于指定值的社团
    List<Club> findByRemainingQuotaGreaterThan(Integer quota);

    // 根据社团名称模糊搜索
    List<Club> findByClubNameContaining(String clubName);

    // 更新剩余名额
    @Modifying
    @Query("UPDATE Club c SET c.remainingQuota = :remainingQuota WHERE c.clubId = :clubId")
    void updateRemainingQuota(@Param("clubId") Integer clubId, @Param("remainingQuota") Integer remainingQuota);

    // 检查社团名称是否存在
    boolean existsByClubName(String clubName);
}