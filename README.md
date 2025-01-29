# SocialLogin


# 소셜로그인

생성일: 2025년 1월 27일 오후 8:59

<aside>
💡

사용기술

- Spring Security
- JWT
</aside>

## Social Logn (refreshToken 포함 Flow)

![socialLogin](https://github.com/user-attachments/assets/684df042-4150-43b3-9434-b9455310dd91)

---

## 스프링 시큐리티

[Core Configuration :: Spring Security](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html)

- Spring Security에서 자체적으로 처리해주는 경우가 있음
    - OAuth2AutorizationRequestRedirectFilter : 인증서버로 요청 (로그인 화면 및 결과 리다이렉트)
    - OAuth2AuthenticationFilter : 리다이렉트 된 결과로 accesscode 발급
    - OAuth2LoginAuthenticationProvivder : 외부서버에 따라서 로그인 정보를 받는 부분의 커스텀역할
        - 큰 기업의 경우 spring securit가 자동으로 추가해줌
            - ex) google, github, okta …
            - naver, kakao는 직접 추가해줘야함
    - OAutrh2Service 및 OAuth2User의 경우 직접 개발해줘야함 (loadUser() → 로그인정보를 통해서 내부 db로 회원 저장하는 역할)
    - LoginSuccessHandler ( [https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/web/authentication/AuthenticationSuccessHandler.html](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/web/authentication/AuthenticationSuccessHandler.html)) 를 통하여 JWT 및 세션 발급 (JWT 헤더에 명시할 예정)

[OAuth2AuthorizationRequestRedirectFilter (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestRedirectFilter.html)

[OAuth2LoginAuthenticationFilter (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/web/OAuth2LoginAuthenticationFilter.html)

[OAuth2LoginAuthenticationProvider (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/authentication/OAuth2LoginAuthenticationProvider.html)

[OAuth2UserService (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/client/userinfo/OAuth2UserService.html)

[OAuth2User (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/oauth2/core/user/OAuth2User.html)

[AuthenticationSuccessHandler (spring-security-docs 6.4.2 API)](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/web/authentication/AuthenticationSuccessHandler.html)

---

## JWT

- 웹
    - Cookie 사용하여 인증로직 처리
    - 장점 : 프론트에서도 웹브라우저가 자동으로 쿠키를 실어주므로, 따로 개발하지 않아도 됨
    - 단점 : cors 설정 및 cookie 설정이 필요함
- native 앱
    - 헤더를 사용해서 인증로직 처리
    - 장점: 앱의 경우 쿠키를 다루기가 매우 까다롭다( 매 요청마다 쿠키를 실어서 보내는 코드를 사용해야함 ), cors 설정을 하지 않아도된다.
    

---

<pre>

A. 문제점)

- Cookie처리의 까다로움을 회피하고자 → 헤더 사용 (Authorization Beaer [token)](https://www.notion.so/token-188cc60c603b8004985ae4f885791f12?pvs=21)
    - 현재구조에서 redirect로 보낼려하였지만, 리다이렉트할경우 header값을 볼수가없는 문제 존재
    - 해결책) body()로 보내거나, query url로 보내거나, 프론트에서 sdk를 이용해 로그인을 처리하거나
</pre>

<pre>

A. 해결책)

## cookie 처리

<pre>
💡

웹 브라우저

- 웹브라우저의 경우, 쿠키처리를 하면 프론트와 백엔드간의 따로 처리해줘야하는 일이 줄어들게된다 (브라우저 지원)
- cookie값에 따른 cors 설정만 해주면됨
</pre>

- 모바일 앱
    - 해당 모바일의 경우 브라우저 지원이 따로 없기에, 직접 쿠키를 관리해야하는 번거러움이 존재
    - 기기 전용 저장 공간이 있기에, 따로 jwt 토큰을 발급해주는 방법을 구상
    
    <pre>
    💡
    
    주는 방법)
    
    1. JWT를 json 바디로 바로 리다이렉트하는 방식
        - 리다이렉트할때에, 헤더값을 볼 수 없기에 헤더에 실는것은 불가능
        - 리다이렉트시 내용들은 로그기록에 남기에 보안상 취약
        - 리다이렉트의 내용은 쿼리파라미터로 넘어가는 경우가 대다수
    2. JWT 대신해서 짧은 인증코드를 sendRedirect로 하고, 그 인증코드와 대조되는 jwt 토큰을 POST요청으로 요청
        - 외부에 노출되는 코드는 일회성의 코드이며, POST요청을 보내기에 보안상의 이점이 존재
        - **POST** 요청의 **Request Body**는
            - 일반적으로 **로그로 찍히지 않고**,
            - **Referrer**에 노출되지 않으며,
            - HTTPS로 암호화되어 있으면 중간에서 가로채기도 어렵습니다.
        
        > 즉, POST로 민감 정보를 주고받는 것이 덜 노출되고, OAuth2 표준에서 권장하는 **'코드 교환 방식'**을 따르게 됩니다.
        > 
        
        <pre>
        💡
        
        - key, value형태
            - value ⇒ jwtToken, createTime
                - createTime으로 만료시간 지정 (accessToken처럼 짧음 → 현재 1분)
                - 만료 전에 프론트에서 헤더에 jwtToken을 실어야함
            - UUID 가 key값임
            - 하위 주소로 POST 요청
        </pre>
        
    3. JWT를 Response Body에 json형태로 실어서 보내기
        - 직접 body에 전송할경우, https 통신을 사용하여 전송해야함
        - 아니면, body내용을 암호화해서 전달하는 방식도 존재
        - refresh token 재발급 과정도 json응답이기에 통일된 구조를 사용
    </pre>
    

</pre>

<pre>

B. 문제점) successHandler 호출안됨

- 호출이 아예안됨 → social 로그인은 처리가됨 (근데 successHandler가 작동안해서 token이 안넘어감)

`.successHandler(customSuccessHandler));`

</pre>

<pre>


B. 해결책)

## successHandler

<pre>


`public class DefaultRedirectStrategy implements RedirectStrategy {    protected final Log logger = LogFactory.*getLog*(this.getClass());    private boolean contextRelative;    private HttpStatus statusCode;    public DefaultRedirectStrategy() {        this.statusCode = HttpStatus.*FOUND*;    }    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {        String redirectUrl = this.calculateRedirectUrl(request.getContextPath(), url);        redirectUrl = response.encodeRedirectURL(redirectUrl);        if (this.logger.isDebugEnabled()) {            this.logger.debug(LogMessage.*format*("Redirecting to %s", redirectUrl));        }        if (this.statusCode == HttpStatus.*FOUND*) {            response.sendRedirect(redirectUrl);        } else {            response.setHeader("Location", redirectUrl);            response.setStatus(this.statusCode.value());            response.getWriter().flush();        }    }`

</pre>

- defaultRedirectStategy가 호출됨 → 기본 리다이렉트
    - securityConfig의 debug모드를 이용해서 호출이 되지 않는다는 것을 확인
- CustomSuccessHandler → Override의 문제가 있었음 (해결)

```java
//일반적인 successHandler
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {}

//커스텀 혹은 특별한 필터체인을 위해서 사용되는 오버로딩된 successHandler
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {}
```

</pre>

---
