package com.example.society.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity//该类会映射到数据库中的表，必须有@Id（只能有一个）标识主键
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//定义主键生成策略，当前为自增
    @Column(name = "student_id")//该JPA指定作用，当前为指定数据库中列的名称
    private Integer studentId;

    @Column(name = "student_num",nullable = false/*是否允许为null*/,unique = true/*是否具有唯一约束*/,length = 20/*字符串类型列最大长度*/)
    private String studentNum;

    @Column(name = "student_name",nullable = false,length = 20)
    private String studentName;

    @Column(name = "password",length = 11)
    private String password;

    @Column(name = "phone",length = 11)
    private String phone;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public Student() {

    }
    public Student(Integer studentId, String studentNum, String studentName, String password, String phone, LocalDateTime createTime) {
        this.studentId = studentId;
        this.studentNum = studentNum;
        this.studentName = studentName;
        this.password = password;
        this.phone = phone;
        this.createTime = createTime;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentNum='" + studentNum + '\'' +
                ", studentName='" + studentName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
