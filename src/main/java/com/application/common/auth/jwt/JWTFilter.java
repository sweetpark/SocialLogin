package com.application.common.auth.jwt;


import com.application.common.auth.dto.oauth2Dto.CustomOAuth2User;
import com.application.common.auth.dto.oauth2Dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;



@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = getAccessToken(request);


        if(accessToken == null){
            log.info("accessToken null");
            filterChain.doFilter(request,response);
        }else{

            if(jwtUtil.isAccessExpired(accessToken)){
                log.info("Access Token expired");
                // 401 error 로 redirect
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401 상태코드 설정
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Access token expired\", \"code\": 401}");
                response.flushBuffer();

            }else{
                //정상처리
                log.info("Access Token is valid");
                setSecurityContext(jwtUtil, accessToken);
                filterChain.doFilter(request,response);
            }

        }
    }





    private String getAccessToken(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null){
            return accessToken;
        }else{
            accessToken = accessToken.substring(7);
            return accessToken;
        }

    }


    private void setSecurityContext(JWTUtil jwtUtil, String accessToken) {
        String credentialId = jwtUtil.getCredentialId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserDTO userDTO = new UserDTO(credentialId, role);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

    }
}
