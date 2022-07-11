package ua.od.wind.dao;


import org.springframework.stereotype.Repository;
import ua.od.wind.model.Sensor;
import ua.od.wind.model.Wind;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class WindDAO {

    //private final SessionFactory sessionFactory;

    @PersistenceContext
    private EntityManager em;

//    @Autowired
//    private Environment env;

//    @Autowired
//    public WindDAO(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }

    public List<Wind> getRawWindData(int sensorId, int dataLimit, int dataOffset ) {

       TypedQuery<Wind> query  = em.createQuery("SELECT w from windTable w where w.sensorId = :sid order by w.timestamp", Wind.class)
                .setParameter("sid", sensorId)
                .setFirstResult(dataOffset)
                .setMaxResults(dataLimit);
        return query.getResultList();
    }

    public void saveWind(Wind wind) {
        em.getTransaction().begin();
        em.persist(wind);
        em.getTransaction().commit();
    }

    public void removeWind() {
    }


    public Sensor getSensorById(int id) {
        int test =1;
        return em.find(Sensor.class, id);
    }

    public Sensor getSensorByImei(String imei) {
        TypedQuery<Sensor> query  = em.createQuery("SELECT s from sensorsTable s where s.imei = :imei", Sensor.class)
                .setParameter("imei", imei);
        return query.getSingleResult();
    }

    public List<Sensor> getEnabledSensors() {
        TypedQuery<Sensor> query  = em.createQuery("SELECT s from sensorsTable s where s.enabled = 1", Sensor.class);
        return query.getResultList();
    }


}
