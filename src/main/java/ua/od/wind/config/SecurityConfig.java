package ua.od.wind.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import ua.od.wind.security.UserDetailsServiceImpl;
import ua.od.wind.service.LoginHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final LoginHandler loginHandler;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, BCryptPasswordEncoder passwordEncoder, DataSource dataSource) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.loginHandler = new LoginHandler("/");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                //.debug(true)
                .ignoring()
                .antMatchers("/img/**", "/about", "/contact", "/save", "/callback");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/pay").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")

                // set user status by days passed from payment.
                //don't use .defaultSuccessUrl() because it override successHandler method and set own handler
                .successHandler(loginHandler())
                .permitAll()

                .and()
                .rememberMe()
                .rememberMeServices(rememberMeServices())
        ;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }


    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        return daoAuthenticationProvider;
    }


    //Using persistent remember me will cause "Invalid remember-me token (Series/token) mismatch"
    //because of loading of authorised content (limited access images) in several requests from browser
    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices result = new TokenBasedRememberMeServices("JHGsf6asdfghj234J", userDetailsServiceImpl);
        result.setTokenValiditySeconds(365 * 24 * 3600);
        return result;
    }


    @Bean
    public LoginHandler loginHandler() {
        return new LoginHandler("/");
    }


}
