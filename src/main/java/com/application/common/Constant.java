package com.application.common;

public class Constant {

    public final static String FRONT_CALLBACK_URI = "myapp://callback?uuid=";  //
    public final static String POST_CLIENT_TOKEN_CALLBACK_URI = "https://localhost:8080/api/token"; //ex) https://localhost:3000/api/token 로그인 성공 후 토큰값 전달
    public final static String FRONT_SEND_REDIRECT = "http://localhost:8080/api/test"; // ex) "http://localhost:3000/api/test" 로그인 성공 후 리다이렉트
    public final static String FRONT_LOGIN_FAIL_REDIRECT = "http://localhost:8080/login"; // ex) 로그인 실패시 리다이렉트
    public final static String FRONT_OAUTH_REDIRECT_CODE_URI = "http://localhost:8080/api/redirect/test?code="; // ex) 리다이렉트 할 경로 "myapp://oauth-callback?code="

}
