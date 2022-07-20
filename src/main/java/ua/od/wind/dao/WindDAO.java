package ua.od.wind.dao;

import ua.od.wind.model.Sensor;
import ua.od.wind.model.Wind;

import java.util.List;

public interface WindDAO {

    List<Wind> getRawWindData(int sensorId, int dataLimit, int dataOffset);

    void saveWind(Wind wind);

    void removeWind(int sensorId);

    Sensor getSensorById(int id);

    Sensor getSensorByImei(String imei);

    List<Sensor> getEnabledSensors();
}
