package com.example.ip_mobileapp.Model;

public class Alarm
{


    public String getAdditional_text() {
        return additional_text;
    }

    public void setAdditional_text(String additional_text) {
        this.additional_text = additional_text;
    }

    public Integer getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(Integer alarm_type) {
        this.alarm_type = alarm_type;
    }

    public Alarm(String additional_text, Integer alarm_type) {
        this.additional_text = additional_text;
        this.alarm_type = alarm_type;
    }

    public Alarm() {
    }

    private String additional_text;
    private Integer alarm_type;
}