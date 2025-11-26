// ClubService.java
package com.example.society.service;

import com.example.society.model.Club;
import java.util.List;
import java.util.Optional;

public interface ClubService {

    // 获取所有社团
    List<Club> findAllClubs();

    // 根据ID查找社团
    Optional<Club> findClubById(Integer clubId);

    // 根据名称查找社团
    Optional<Club> findClubByName(String clubName);

    // 根据类型查找社团
    List<Club> findClubsByType(String clubType);

    // 根据名称模糊搜索社团
    List<Club> searchClubsByName(String clubName);

    // 查找剩余名额大于指定值的社团
    List<Club> findClubsWithRemainingQuotaGreaterThan(Integer quota);

    // 创建新社团
    Club createClub(Club club);

    // 更新社团信息
    Club updateClub(Club club);

    // 更新剩余名额
    void updateRemainingQuota(Integer clubId, Integer remainingQuota);

    // 删除社团
    void deleteClub(Integer clubId);

    // 检查社团名称是否存在
    boolean existsByClubName(String clubName);
}