package org.zerock.b01.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.Custom403Handler;
import org.zerock.b01.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class CustomSecurityConfig {

    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;

    @Bean   // 비밀번호를 암호화하기 위한 빈을 생성합니다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean   // 소셜 로그인 성공 후 처리하는 핸들러를 설정합니다.
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

    @Bean   // HTTP 보안 설정을 구성합니다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("------------configure-------------------");

        //커스텀 로그인 페이지
        http.formLogin().loginPage("/member/login");
        //CSRF 토큰 비활성화
        http.csrf().disable();
        // RememberMe 설정
        http.rememberMe()
            .key("12345678")
            .tokenRepository(persistentTokenRepository())
            .userDetailsService(userDetailsService)
            .tokenValiditySeconds(60*60*24*30);

        // 403 핸들러 설정
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler()); //403

        // OAuth2 로그인 설정
        http.oauth2Login().loginPage("/member/login").successHandler(authenticationSuccessHandler());

        return http.build();
    }




    @Bean   //접근이 거부되었을 때의 핸들러를 설정합니다.
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }


    @Bean   //정적 리소스에 대한 보안을 설정합니다.
    public WebSecurityCustomizer webSecurityCustomizer() {

        log.info("------------web configure-------------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }
    @Bean   //RememberMe 기능을 위한 토큰 저장소를 설정합니다.
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
}
