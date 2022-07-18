package ua.od.wind.dao;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.od.wind.model.Sensor;
import ua.od.wind.model.Wind;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class WindDAO {

    @PersistenceContext
    private EntityManager em;

    public List<Wind> getRawWindData(int sensorId, int dataLimit, int dataOffset) {
        TypedQuery<Wind> query = em.createQuery("SELECT w from windTable w where w.sensorId = :sid order by w.timestamp desc", Wind.class)
                .setParameter("sid", sensorId)
                .setFirstResult(dataOffset)
                .setMaxResults(dataLimit);
        return query.getResultList();
    }

    @Transactional
    public void saveWind(Wind wind) {
        em.persist(wind);
    }

    public void removeWind(int sensorId) {
        //DELETE FROM wind ORDER BY timestamp limit 1
        TypedQuery<Wind> query = em.createQuery("SELECT w from windTable w where w.sensorId = :sid order by w.timestamp desc", Wind.class)
                .setParameter("sid", sensorId)
                .setMaxResults(1);
        Wind wind = query.getSingleResult();
        em.remove(wind);

    }


    public Sensor getSensorById(int id) {
        return em.find(Sensor.class, id);
    }

    public Sensor getSensorByImei(String imei) {
        TypedQuery<Sensor> query = em.createQuery("SELECT s from sensorsTable s where s.imei = :imei", Sensor.class)
                .setParameter("imei", imei);
        return query.getSingleResult();
    }

    public List<Sensor> getEnabledSensors() {
        TypedQuery<Sensor> query = em.createQuery("SELECT s from sensorsTable s where s.enabled = 1", Sensor.class);
        return query.getResultList();
    }


}
