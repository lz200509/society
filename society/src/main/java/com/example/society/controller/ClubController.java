// ClubController.java (更新版本)
package com.example.society.controller;

import com.example.society.common.*;
import com.example.society.model.Club;
import com.example.society.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    // 获取所有社团
    @GetMapping
    public Result<List<Club>> getAllClubs() {
        try {
            List<Club> clubs = clubService.findAllClubs();
            return ControllerUtils.buildListResult(clubs);
        } catch (Exception e) {
            throw new BusinessException("获取社团列表失败");
        }
    }

    // 根据ID获取社团
    @GetMapping("/{clubId}")
    public Result<Club> getClubById(@PathVariable Integer clubId) {
        try {
            Optional<Club> club = clubService.findClubById(clubId);
            return club.map(ControllerUtils::buildSingleResult)
                    .orElse(Result.notFound("社团不存在"));
        } catch (Exception e) {
            throw new BusinessException("获取社团信息失败");
        }
    }

    // 根据名称获取社团
    @GetMapping("/name/{clubName}")
    public Result<Club> getClubByName(@PathVariable String clubName) {
        try {
            Optional<Club> club = clubService.findClubByName(clubName);
            return club.map(ControllerUtils::buildSingleResult)
                    .orElse(Result.notFound("社团不存在"));
        } catch (Exception e) {
            throw new BusinessException("获取社团信息失败");
        }
    }

    // 根据类型获取社团
    @GetMapping("/type/{clubType}")
    public Result<List<Club>> getClubsByType(@PathVariable String clubType) {
        try {
            List<Club> clubs = clubService.findClubsByType(clubType);
            return ControllerUtils.buildListResult(clubs);
        } catch (Exception e) {
            throw new BusinessException("获取社团列表失败");
        }
    }

    // 搜索社团（名称模糊匹配）
    @GetMapping("/search")
    public Result<List<Club>> searchClubs(@RequestParam String clubName) {
        try {
            List<Club> clubs = clubService.searchClubsByName(clubName);
            return ControllerUtils.buildListResult(clubs);
        } catch (Exception e) {
            throw new BusinessException("搜索社团失败");
        }
    }

    // 获取有剩余名额的社团
    @GetMapping("/available")
    public Result<List<Club>> getAvailableClubs(@RequestParam(defaultValue = "0") Integer minQuota) {
        try {
            List<Club> clubs = clubService.findClubsWithRemainingQuotaGreaterThan(minQuota);
            return ControllerUtils.buildListResult(clubs);
        } catch (Exception e) {
            throw new BusinessException("获取可用社团失败");
        }
    }

    // 创建新社团
    @PostMapping
    public Result<Club> createClub(@RequestBody Club club) {
        try {
            // 检查社团名称是否已存在
            if (clubService.existsByClubName(club.getClubName())) {
                throw new BusinessException(ResultCode.CONFLICT.getCode(), "社团名称已存在");
            }

            Club savedClub = clubService.createClub(club);
            return Result.success(ResultCode.CREATED.getMessage(), savedClub);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("创建社团失败");
        }
    }

    // 更新社团信息
    @PutMapping("/{clubId}")
    public Result<Club> updateClub(@PathVariable Integer clubId, @RequestBody Club club) {
        try {
            // 确保路径中的ID与请求体中的ID一致
            if (!clubId.equals(club.getClubId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "ID不匹配");
            }

            Club updatedClub = clubService.updateClub(club);
            return ControllerUtils.buildSingleResult(updatedClub);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("更新社团失败");
        }
    }

    // 更新社团剩余名额
    @PutMapping("/{clubId}/quota")
    public Result<String> updateClubQuota(@PathVariable Integer clubId, @RequestParam Integer remainingQuota) {
        try {
            clubService.updateRemainingQuota(clubId, remainingQuota);
            return ControllerUtils.buildSuccessResult("更新名额成功");
        } catch (Exception e) {
            throw new BusinessException("更新名额失败");
        }
    }

    // 删除社团
    @DeleteMapping("/{clubId}")
    public Result<String> deleteClub(@PathVariable Integer clubId) {
        try {
            clubService.deleteClub(clubId);
            return ControllerUtils.buildSuccessResult("删除社团成功");
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "社团不存在");
        } catch (Exception e) {
            throw new BusinessException("删除社团失败");
        }
    }

    // 检查社团名称是否存在
    @GetMapping("/check-name")
    public Result<Boolean> checkClubNameExists(@RequestParam String clubName) {
        try {
            boolean exists = clubService.existsByClubName(clubName);
            return ControllerUtils.buildSingleResult(exists);
        } catch (Exception e) {
            throw new BusinessException("检查社团名称失败");
        }
    }
}