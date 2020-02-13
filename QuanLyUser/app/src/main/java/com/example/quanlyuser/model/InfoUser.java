package com.example.quanlyuser.model;

public class InfoUser {
    String Id, Time, ViDo, KinhDo, MaUser, TenUser,Tuoi;

    public InfoUser(String id, String time, String viDo, String kinhDo, String maUser, String tenUser, String tuoi) {
        Id = id;
        Time = time;
        ViDo = viDo;
        KinhDo = kinhDo;
        MaUser = maUser;
        TenUser = tenUser;
        Tuoi = tuoi;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getViDo() {
        return ViDo;
    }

    public void setViDo(String viDo) {
        ViDo = viDo;
    }

    public String getKinhDo() {
        return KinhDo;
    }

    public void setKinhDo(String kinhDo) {
        KinhDo = kinhDo;
    }

    public String getMaUser() {
        return MaUser;
    }

    public void setMaUser(String maUser) {
        MaUser = maUser;
    }

    public String getTenUser() {
        return TenUser;
    }

    public void setTenUser(String tenUser) {
        TenUser = tenUser;
    }

    public String getTuoi() {
        return Tuoi;
    }

    public void setTuoi(String tuoi) {
        Tuoi = tuoi;
    }
}
