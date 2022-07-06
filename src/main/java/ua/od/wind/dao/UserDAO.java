package ua.od.wind.dao;

import org.springframework.stereotype.Repository;
import ua.od.wind.model.User;
import ua.od.wind.security.UserDetailsServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Repository
public class UserDAO {




    @PersistenceContext
    private EntityManager em;

//    public Optional<UserDetailsImpl> findById(Long id) {
//        return  Optional.of(em.find(UserDetailsImpl.class, id));
//    }

    public Optional<User> findByUsername(String username){

        TypedQuery<User> query  = em.createQuery("SELECT u from User u where u.username = :name", User.class)
                .setParameter("name", username);
        return  Optional.of(query.getSingleResult());


    }
//    public void save(UserDetailsImpl user) {
//        em.getTransaction().begin();
//        em.persist(user);
//        em.getTransaction().commit();
//    }
//
//    public List<UserDetailsImpl> findAll() {
//return null;
//    }
}
