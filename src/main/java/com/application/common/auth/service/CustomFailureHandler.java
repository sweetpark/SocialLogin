package com.application.common.auth.service;

import com.application.common.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        queryRedirect(response);
    }

    private void queryRedirect(HttpServletResponse response) throws IOException{
        String query = String.format(Constant.FRONT_CALLBACK_URI+"%s", "");
        response.sendRedirect(query);
    }
}
