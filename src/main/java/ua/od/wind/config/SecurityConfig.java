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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
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
        this.loginHandler = new LoginHandler("/main");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                //.debug(true)
                .ignoring()
                .antMatchers("/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers( "/pay").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .formLogin()
                    .loginPage("/login")
                .successHandler(loginHandler)
                .defaultSuccessUrl("/main",true)
                .permitAll()

                .and()
                    .rememberMe().key("JHGsf6asdfghj234J").tokenValiditySeconds(365*24*3600)
                    .tokenRepository(persistentTokenRepository())
                ;


//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/shifted", "/register", "/callback", "/pay").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login").permitAll()
//                .defaultSuccessUrl("/main")
//                .and()
//                .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(365*24*3600)
//               ;
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

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
        tokenRepo.setDataSource(dataSource);
        return tokenRepo;
    }




}
