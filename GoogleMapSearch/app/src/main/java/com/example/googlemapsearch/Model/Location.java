package com.example.googlemapsearch.Model;

public class Location {
    String Id,Time,ViDo,KinhDo;

    public Location(String id, String time, String viDo, String kinhDo) {
        Id = id;
        Time = time;
        ViDo = viDo;
        KinhDo = kinhDo;
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
}
