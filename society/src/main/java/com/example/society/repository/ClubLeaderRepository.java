package com.example.society.repository;

import com.example.society.model.Club;
import com.example.society.model.ClubLeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubLeaderRepository extends JpaRepository<ClubLeader, Integer> {

    // 根据负责人编号查找
    Optional<ClubLeader> findByLeaderNum(String leaderNum);

    // 根据社团查找负责人
    Optional<ClubLeader> findByClub(Club club);

    // 根据社团ID查找负责人
    @Query("SELECT cl FROM ClubLeader cl WHERE cl.club.clubId = :clubId")
    Optional<ClubLeader> findByClubId(@Param("clubId") Integer clubId);

    // 根据负责人姓名查找
    List<ClubLeader> findByLeaderName(String leaderName);

    // 检查负责人编号是否存在
    boolean existsByLeaderNum(String leaderNum);

    // 验证负责人登录
    @Query("SELECT cl FROM ClubLeader cl WHERE cl.leaderNum = :leaderNum AND cl.password = :password")
    Optional<ClubLeader> findByLeaderNumAndPassword(@Param("leaderNum") String leaderNum, @Param("password") String password);

    // 根据手机号查找负责人
    Optional<ClubLeader> findByPhone(String phone);
}