// StudentServiceImpl.java
package com.example.society.service.impl;

import com.example.society.model.Student;
import com.example.society.repository.StudentRepository;
import com.example.society.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentById(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentByStudentNum(String studentNum) {
        return studentRepository.findByStudentNum(studentNum);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentsByName(String studentName) {
        return studentRepository.findByStudentName(studentName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchStudentsByName(String studentName) {
        return studentRepository.findByStudentNameContaining(studentName);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentByPhone(String phone) {
        return studentRepository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> login(String studentNum, String password) {
        return studentRepository.findByStudentNumAndPassword(studentNum, password);
    }

    @Override
    public Student registerStudent(Student student) {
        // 设置创建时间
        if (student.getCreateTime() == null) {
            student.setCreateTime(LocalDateTime.now());
        }

        // 检查学号是否已存在
        if (studentRepository.existsByStudentNum(student.getStudentNum())) {
            throw new RuntimeException("学号已存在: " + student.getStudentNum());
        }

        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Student student) {
        if (!studentRepository.existsById(student.getStudentId())) {
            throw new RuntimeException("学生不存在，ID: " + student.getStudentId());
        }
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Integer studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("学生不存在，ID: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentNum(String studentNum) {
        return studentRepository.existsByStudentNum(studentNum);
    }
}