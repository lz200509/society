package com.example.society.model;


import jakarta.persistence.*;

@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Integer clubId;

    @Column(name = "club_name",nullable = false,unique = true,length = 50)
    private String clubName;

    @Column(name = "club_type",nullable = false,length = 20)
    private String clubType;

    @Column(name = "club_intro",columnDefinition = "TEXT")
    private String clubIntro;

    @Column(name = "total_quota",nullable = false)
    private Integer totalQuota;

    @Column(name = "remaining_quota",nullable = false)
    private Integer remainingQuota;

    @Column(name = "logo_url",length = 200)
    private String logoUrl;

    public Club() {

    }

    public Club(Integer clubId, String clubName, String clubType, String clubIntro, Integer totalQuota, Integer remainingQuota, String logoUrl) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.clubType = clubType;
        this.clubIntro = clubIntro;
        this.totalQuota = totalQuota;
        this.remainingQuota = remainingQuota;
        this.logoUrl = logoUrl;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubType() {
        return clubType;
    }

    public void setClubType(String clubType) {
        this.clubType = clubType;
    }

    public Integer getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(Integer totalQuota) {
        this.totalQuota = totalQuota;
    }

    public String getClubIntro() {
        return clubIntro;
    }

    public void setClubIntro(String clubIntro) {
        this.clubIntro = clubIntro;
    }

    public Integer getRemainingQuota() {
        return remainingQuota;
    }

    public void setRemainingQuota(Integer remainingQuota) {
        this.remainingQuota = remainingQuota;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public String toString() {
        return "Club{" +
                "clubId=" + clubId +
                ", clubName='" + clubName + '\'' +
                ", clubType='" + clubType + '\'' +
                ", clubIntro='" + clubIntro + '\'' +
                ", totalQuota=" + totalQuota +
                ", remainingQuota=" + remainingQuota +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
}
