// ClubLeaderController.java
package com.example.society.controller;

import com.example.society.common.Result;
import com.example.society.common.ResultCode;
import com.example.society.dto.LoginRequest;
import com.example.society.model.ClubLeader;
import com.example.society.service.ClubLeaderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/leaders")
public class ClubLeaderController {

    @Autowired
    private ClubLeaderService clubLeaderService;


    // 负责人登录
    @PostMapping("/login")
    public Result<ClubLeader> login(@RequestBody Map<String, String> loginData) {
        try {
            log.info("接收到登录请求: {}", loginData);

            String leaderNum = loginData.get("leaderNum");
            String password = loginData.get("password");

            if (leaderNum == null || password == null) {
                return Result.error(ResultCode.BAD_REQUEST.getCode(), "参数不能为空");
            }

            Optional<ClubLeader> leader = clubLeaderService.login(leaderNum, password);
            if (leader.isPresent()) {
                return Result.success(ResultCode.LOGIN_SUCCESS.getMessage(), leader.get());
            } else {
                return Result.error(ResultCode.LOGIN_FAILED.getCode(), "负责人编号或密码错误");
            }
        } catch (Exception e) {
            log.error("负责人登录异常: {}", e.getMessage(), e);
            return Result.error(ResultCode.ERROR.getCode(), "登录失败: " + e.getMessage());
        }
    }



    // 获取所有负责人
    @GetMapping
    public ResponseEntity<List<ClubLeader>> getAllLeaders() {
        try {
            List<ClubLeader> leaders = clubLeaderService.findAllLeaders();
            return ResponseEntity.ok(leaders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据ID获取负责人
    @GetMapping("/{leaderId}")
    public ResponseEntity<ClubLeader> getLeaderById(@PathVariable Integer leaderId) {
        try {
            Optional<ClubLeader> leader = clubLeaderService.findLeaderById(leaderId);
            return leader.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据负责人编号获取
    @GetMapping("/leader-num/{leaderNum}")
    public ResponseEntity<ClubLeader> getLeaderByLeaderNum(@PathVariable String leaderNum) {
        try {
            Optional<ClubLeader> leader = clubLeaderService.findLeaderByLeaderNum(leaderNum);
            return leader.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据社团ID获取负责人
    @GetMapping("/club/{clubId}")
    public ResponseEntity<ClubLeader> getLeaderByClubId(@PathVariable Integer clubId) {
        try {
            Optional<ClubLeader> leader = clubLeaderService.findLeaderByClubId(clubId);
            return leader.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据负责人姓名获取
    @GetMapping("/name/{leaderName}")
    public ResponseEntity<List<ClubLeader>> getLeadersByName(@PathVariable String leaderName) {
        try {
            List<ClubLeader> leaders = clubLeaderService.findLeadersByName(leaderName);
            return ResponseEntity.ok(leaders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据手机号获取负责人
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ClubLeader> getLeaderByPhone(@PathVariable String phone) {
        try {
            Optional<ClubLeader> leader = clubLeaderService.findLeaderByPhone(phone);
            return leader.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 创建负责人
    @PostMapping
    public ResponseEntity<?> createLeader(@RequestBody ClubLeader leader) {
        try {
            ClubLeader savedLeader = clubLeaderService.createLeader(leader);
            return ResponseEntity.ok(savedLeader);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("创建负责人失败: " + e.getMessage());
        }
    }

    // 更新负责人信息
    @PutMapping("/{leaderId}")
    public ResponseEntity<?> updateLeader(@PathVariable Integer leaderId, @RequestBody ClubLeader leader) {
        try {
            // 确保路径中的ID与请求体中的ID一致
            if (!leaderId.equals(leader.getLeaderId())) {
                return ResponseEntity.badRequest().body("ID不匹配");
            }

            ClubLeader updatedLeader = clubLeaderService.updateLeader(leader);
            return ResponseEntity.ok(updatedLeader);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("更新负责人信息失败: " + e.getMessage());
        }
    }

    // 删除负责人
    @DeleteMapping("/{leaderId}")
    public ResponseEntity<?> deleteLeader(@PathVariable Integer leaderId) {
        try {
            clubLeaderService.deleteLeader(leaderId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除负责人失败: " + e.getMessage());
        }
    }

    // 检查负责人编号是否存在
    @GetMapping("/check-leader-num")
    public ResponseEntity<Boolean> checkLeaderNumExists(@RequestParam String leaderNum) {
        try {
            boolean exists = clubLeaderService.existsByLeaderNum(leaderNum);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}