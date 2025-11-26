// ClubLeaderService.java
package com.example.society.service;

import com.example.society.model.ClubLeader;
import java.util.List;
import java.util.Optional;

public interface ClubLeaderService {

    // 获取所有社团负责人
    List<ClubLeader> findAllLeaders();

    // 根据ID查找负责人
    Optional<ClubLeader> findLeaderById(Integer leaderId);

    // 根据负责人编号查找
    Optional<ClubLeader> findLeaderByLeaderNum(String leaderNum);

    // 根据社团查找负责人
    Optional<ClubLeader> findLeaderByClubId(Integer clubId);

    // 根据负责人姓名查找
    List<ClubLeader> findLeadersByName(String leaderName);

    // 根据手机号查找负责人
    Optional<ClubLeader> findLeaderByPhone(String phone);

    // 负责人登录验证
    Optional<ClubLeader> login(String leaderNum, String password);

    // 创建负责人
    ClubLeader createLeader(ClubLeader leader);

    // 更新负责人信息
    ClubLeader updateLeader(ClubLeader leader);

    // 删除负责人
    void deleteLeader(Integer leaderId);

    // 检查负责人编号是否存在
    boolean existsByLeaderNum(String leaderNum);
}