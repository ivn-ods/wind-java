package ua.od.wind.models;

import javax.persistence.*;

@Entity(name = "windTable")
@Table(name = "wind")
public class Wind {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sensor_id")
    private int sensorId;

    private int min;
    private int mid;
    private int max;
    private int temp;
    private int dir;
    private int timestamp;
    private int v0;
    private int v1;



    public void setId(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }




    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getDir() {
        return dir / 100;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getV0() {
        return v0;
    }

    public void setV0(int v0) {
        this.v0 = v0;
    }

    public int getV1() {
        return v1;
    }

    public void setV1(int v1) {
        this.v1 = v1;
    }

}