package com.example.mymeetings;

public class MeetingListModel {
    public String name;
    public String sapid;

    public MeetingListModel(){}

    public MeetingListModel(String name, String sapid) {
        this.name = name;
        this.sapid = sapid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSapid() {
        return sapid;
    }

    public void setSapid(String sapid) {
        this.sapid = sapid;
    }
}
