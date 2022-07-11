package ua.od.wind.models;

import javax.persistence.*;

@Entity(name = "sensorsTable")
@Table(name = "sensors")

public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "en_name")
    private String enName;
    private String imei;
    @Column(name = "speed_factor")
    private int speedFactor;
    @Column(name = "dir_factor")
    private int dirFactor;
    private int enabled;
    @Column(name = "dir_enabled")
    private int dirEnabled;
    @Column(name = "temp_enabled")
    private int tempEnabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(int speedFactor) {
        this.speedFactor = speedFactor;
    }

    public int getDirFactor() {
        return dirFactor;
    }

    public void setDirFactor(int dirFactor) {
        this.dirFactor = dirFactor;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getDirEnabled() {
        return dirEnabled;
    }

    public void setDirEnabled(int dirEnabled) {
        this.dirEnabled = dirEnabled;
    }

    public int getTempEnabled() {
        return tempEnabled;
    }

    public void setTempEnabled(int tempEnabled) {
        this.tempEnabled = tempEnabled;
    }
}
