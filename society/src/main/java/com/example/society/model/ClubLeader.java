package com.example.society.model;

import jakarta.persistence.*;

@Entity
@Table(name = "club_leader")
public class ClubLeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leader_id")
    private Integer leaderId;

    @Column(name = "leader_num",nullable = false,unique = true,length = 20)
    private String leaderNum;

    @Column(name = "leader_name",nullable = false,unique = false,length = 20)
    private String leaderName;

    @Column(name = "password",length = 11)
    private String password;

    @Column(name = "phone",length = 11)
    private String phone;

    @ManyToOne//多对一关系映射
    @JoinColumn(name = "club_id")//实体关联所对应的数据库外键列
    private Club club;

    public ClubLeader() {

    }

    public ClubLeader(Integer leaderId, String leaderNum, String leaderName, String password, String phone, Club club) {
        this.leaderId = leaderId;
        this.leaderNum = leaderNum;
        this.leaderName = leaderName;
        this.password = password;
        this.phone = phone;
        this.club = club;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderNum() {
        return leaderNum;
    }

    public void setLeaderNum(String leaderNum) {
        this.leaderNum = leaderNum;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    @Override
    public String toString() {
        return "ClubLeader{" +
                "leaderId=" + leaderId +
                ", leaderNum='" + leaderNum + '\'' +
                ", leaderName='" + leaderName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", club=" + club +
                '}';
    }
}
