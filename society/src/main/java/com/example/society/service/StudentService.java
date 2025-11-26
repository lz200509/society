// StudentService.java
package com.example.society.service;

import com.example.society.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    // 获取所有学生
    List<Student> findAllStudents();

    // 根据ID查找学生
    Optional<Student> findStudentById(Integer studentId);

    // 根据学号查找学生
    Optional<Student> findStudentByStudentNum(String studentNum);

    // 根据姓名查找学生
    List<Student> findStudentsByName(String studentName);

    // 根据姓名模糊搜索学生
    List<Student> searchStudentsByName(String studentName);

    // 根据手机号查找学生
    Optional<Student> findStudentByPhone(String phone);

    // 学生登录验证
    Optional<Student> login(String studentNum, String password);

    // 注册新学生
    Student registerStudent(Student student);

    // 更新学生信息
    Student updateStudent(Student student);

    // 删除学生
    void deleteStudent(Integer studentId);

    // 检查学号是否存在
    boolean existsByStudentNum(String studentNum);
}