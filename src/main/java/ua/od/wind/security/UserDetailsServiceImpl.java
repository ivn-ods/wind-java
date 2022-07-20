package ua.od.wind.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.od.wind.dao.UserDAOimpl;
import ua.od.wind.model.User;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    //private final UserRepository userRepository;
    private final UserDAOimpl userDAOimpl;

    @Autowired
    public UserDetailsServiceImpl(UserDAOimpl userDAOimpl) {
        this.userDAOimpl = userDAOimpl;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAOimpl.getUserListByUsername(username).stream().findFirst().orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        return SecurityUser.fromUser(user);
    }


}
