package ua.od.wind.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.od.wind.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    public List<User> getUserListByUsername(String username){
        TypedQuery<User> query  = em.createQuery("SELECT u from User u where u.username = :name", User.class)
                .setParameter("name", username);
        return  query.getResultList();

    }

    public Optional<User> findByPaymentId(String paymentId){
        TypedQuery<User> query  = em.createQuery("SELECT u from User u where u.paymentId = :paymentId", User.class)
                .setParameter("paymentId", paymentId);
        return  Optional.of(query.getSingleResult());

    }

    @Transactional
    public void saveUser(User user) {
        em.persist(user);
    }


}
