package com.example.ip_mobileapp.Model;

public class SensorsData {
   private Integer weight;
   private Integer glucose;

   private  Integer blood_pressure;

   private  Integer body_temperature;

    public SensorsData(Integer weight, Integer glucose, Integer blood_pressure, Integer body_temperature) {
        this.weight = weight;
        this.glucose = glucose;
        this.blood_pressure = blood_pressure;
        this.body_temperature = body_temperature;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getGlucose() {
        return glucose;
    }

    public void setGlucose(Integer glucose) {
        this.glucose = glucose;
    }

    public Integer getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(Integer blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public Integer getBody_temperature() {
        return body_temperature;
    }

    public void setBody_temperature(Integer body_temperature) {
        this.body_temperature = body_temperature;
    }
}
