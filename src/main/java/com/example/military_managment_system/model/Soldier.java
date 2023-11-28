package com.example.military_managment_system.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

@Entity
public class Soldier {
    @Id
    private Long regNo;
    private String firstName;
    private String lastName;
    private String Rank;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private String gender;
    private String email;

    public Soldier() {
    }

    public Soldier(Long regNo, String firstName, String lastName, String rank, Date dob, String gender, String email) {
        this.regNo = regNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.Rank = rank;
        this.dob = dob;
        this.email = email;
        this.gender = gender;

    }

    public Long getRegNo() {
        return regNo;
    }

    public void setRegNo(Long regNo) {
        this.regNo = regNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}


