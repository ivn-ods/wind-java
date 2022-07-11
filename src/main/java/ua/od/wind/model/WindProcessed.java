package ua.od.wind.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class WindProcessed{
    private int id;
    private int sensorId;

    private int temp;
    private int day;
    private int hour;
    private int minute;
    private String date;
    private String hourStr;
    private String minuteStr;
    private float min;
    private float mid;
    private float max;
    private float dir;
    private float v0;
    private float v1;

    public String getHourStr() {
        return hourStr;
    }

    public void setHourStr(String hourStr) {
        this.hourStr = hourStr;
    }

    public String getMinuteStr() {
        return minuteStr;
    }

    public void setMinuteStr(String minuteStr) {
        this.minuteStr = minuteStr;
    }

    public int getId() {
        return id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public int getTemp() {
        return temp;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public float getMin() {
        return min;
    }

    public float getMid() {
        return mid;
    }

    public float getMax() {
        return max;
    }

    public float getDir() {
        return dir;
    }

    public float getV0() {
        return v0;
    }

    public float getV1() {
        return v1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }


    public void setMin(float min) {
        this.min = min;
    }

    public void setMid(float mid) {
        this.mid = mid;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setDir(float dir) {
        this.dir = dir;
    }

    public void setV0(float v0) {
        this.v0 = v0;
    }

    public void setV1(float v1) {
        this.v1 = v1;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getDate() {
        return date;
    }
}
