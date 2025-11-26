package com.example.society.repository;

import com.example.society.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // 根据学号查找学生
    Optional<Student> findByStudentNum(String studentNum);

    // 根据姓名查找学生
    List<Student> findByStudentName(String studentName);

    // 根据姓名模糊搜索
    List<Student> findByStudentNameContaining(String studentName);

    // 检查学号是否存在
    boolean existsByStudentNum(String studentNum);

    // 根据手机号查找学生
    Optional<Student> findByPhone(String phone);

    // 验证学生登录
    @Query("SELECT s FROM Student s WHERE s.studentNum = :studentNum AND s.password = :password")
    Optional<Student> findByStudentNumAndPassword(@Param("studentNum") String studentNum, @Param("password") String password);
}