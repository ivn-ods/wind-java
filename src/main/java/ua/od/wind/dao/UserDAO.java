package ua.od.wind.dao;
import ua.od.wind.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

     List<User> getUserListByUsername(String username);

     Optional<User> findByPaymentId(String paymentId);

     Optional<User> getUserByUsername(String username);

     void saveUser(User user);

     void mergeUser(User user);

}
