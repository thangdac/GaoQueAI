package com.GaoQue.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Date;

@Entity
public class RicePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ngay;
    private double luaTuoiHatDai;
    private double luaTuoiLuaThuong;
    private double luaKhoHatDai;
    private double luaKhoLuaThuong;

    // Constructors
    public RicePrice() {}

    public RicePrice(String ngay, double luaTuoiHatDai, double luaTuoiLuaThuong, double luaKhoHatDai, double luaKhoLuaThuong) {
        this.ngay = ngay;
        this.luaTuoiHatDai = luaTuoiHatDai;
        this.luaTuoiLuaThuong = luaTuoiLuaThuong;
        this.luaKhoHatDai = luaKhoHatDai;
        this.luaKhoLuaThuong = luaKhoLuaThuong;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public double getLuaTuoiHatDai() {
        return luaTuoiHatDai;
    }

    public void setLuaTuoiHatDai(double luaTuoiHatDai) {
        this.luaTuoiHatDai = luaTuoiHatDai;
    }

    public double getLuaTuoiLuaThuong() {
        return luaTuoiLuaThuong;
    }

    public void setLuaTuoiLuaThuong(double luaTuoiLuaThuong) {
        this.luaTuoiLuaThuong = luaTuoiLuaThuong;
    }

    public double getLuaKhoHatDai() {
        return luaKhoHatDai;
    }

    public void setLuaKhoHatDai(double luaKhoHatDai) {
        this.luaKhoHatDai = luaKhoHatDai;
    }

    public double getLuaKhoLuaThuong() {
        return luaKhoLuaThuong;
    }

    public void setLuaKhoLuaThuong(double luaKhoLuaThuong) {
        this.luaKhoLuaThuong = luaKhoLuaThuong;
    }
}
