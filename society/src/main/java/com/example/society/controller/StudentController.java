// StudentController.java
package com.example.society.controller;

import com.example.society.common.BusinessException;
import com.example.society.common.Result;
import com.example.society.common.ResultCode;
import com.example.society.model.Student;
import com.example.society.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 学生登录
    @PostMapping("/login")
    public Result<Student> login(@RequestParam String studentNum, @RequestParam String password) {
        try {
            Optional<Student> student = studentService.login(studentNum, password);
            if (student.isPresent()) {
                return Result.success(ResultCode.LOGIN_SUCCESS.getMessage(), student.get());
            } else {
                return Result.error(ResultCode.LOGIN_FAILED.getCode(), "学号或密码错误");
            }
        } catch (Exception e) {
            throw new BusinessException("登录失败: " + e.getMessage());
        }
    }

    // 获取所有学生
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        try {
            List<Student> students = studentService.findAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据ID获取学生
    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer studentId) {
        try {
            Optional<Student> student = studentService.findStudentById(studentId);
            return student.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据学号获取学生
    @GetMapping("/student-num/{studentNum}")
    public ResponseEntity<Student> getStudentByStudentNum(@PathVariable String studentNum) {
        try {
            Optional<Student> student = studentService.findStudentByStudentNum(studentNum);
            return student.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据姓名获取学生
    @GetMapping("/name/{studentName}")
    public ResponseEntity<List<Student>> getStudentsByName(@PathVariable String studentName) {
        try {
            List<Student> students = studentService.findStudentsByName(studentName);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 搜索学生（姓名模糊匹配）
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String studentName) {
        try {
            List<Student> students = studentService.searchStudentsByName(studentName);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据手机号获取学生
    @GetMapping("/phone/{phone}")
    public ResponseEntity<Student> getStudentByPhone(@PathVariable String phone) {
        try {
            Optional<Student> student = studentService.findStudentByPhone(phone);
            return student.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 学生注册
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody Student student) {
        try {
            System.out.println("注册请求数据: " + student);

            Student savedStudent = studentService.registerStudent(student);

            // 返回统一格式的响应
            return ResponseEntity.ok(Result.success(ResultCode.REGISTER_SUCCESS.getMessage(), savedStudent));
        } catch (RuntimeException e) {
            System.out.println("注册业务异常: " + e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(ResultCode.REGISTER_FAILED.getCode(), e.getMessage()));
        } catch (Exception e) {
            System.out.println("注册系统异常: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Result.error("注册失败: " + e.getMessage()));
        }
    }

    // 更新学生信息
    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer studentId, @RequestBody Student student) {
        try {
            // 确保路径中的ID与请求体中的ID一致
            if (!studentId.equals(student.getStudentId())) {
                return ResponseEntity.badRequest().body("ID不匹配");
            }

            Student updatedStudent = studentService.updateStudent(student);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("更新学生信息失败: " + e.getMessage());
        }
    }

    // 删除学生
    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除学生失败: " + e.getMessage());
        }
    }

    // 检查学号是否存在
    @GetMapping("/check-student-num")
    public ResponseEntity<Boolean> checkStudentNumExists(@RequestParam String studentNum) {
        try {
            boolean exists = studentService.existsByStudentNum(studentNum);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}