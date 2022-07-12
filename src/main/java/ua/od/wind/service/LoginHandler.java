package ua.od.wind.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private  UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginHandler(String target) {
        super(target);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        userService.checkUserStatus(authentication);
        super.onAuthenticationSuccess( httpServletRequest,  httpServletResponse,  authentication);
    }
}
