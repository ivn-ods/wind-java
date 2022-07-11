package ua.od.wind.security;


import ua.od.wind.model.User;
//import ua.od.wind.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.od.wind.service.UserService;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    //private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getOptionalUserByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("User doesn't exists"));
        return SecurityUser.fromUser(user);
    }

}
