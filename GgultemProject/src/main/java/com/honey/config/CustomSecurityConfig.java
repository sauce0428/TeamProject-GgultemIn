package com.honey.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.honey.domain.Member;
import com.honey.domain.MemberRole;
import com.honey.filter.JWTCheckFilter;
import com.honey.repository.MemberRepository;
import com.honey.security.handler.APILoginFailHandler;
import com.honey.security.handler.APILoginSuccessHandler;
import com.honey.security.handler.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 프론트엔드에서 오는 교차 출처 요청(CORS)을 이 설정에 따라 허용
		http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
				.configurationSource(corsConfigurationSource()));
		// 세션을 생성하지 않음(stateless). JWT 같은 토큰 기반 인증 시스템에서 사용된다.
		// 로그인 상태를 서버 세션으로 저장하지 않고, 매 요청마다 인증 정보를 전달해야 한다.
		http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		// CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
		// REST API 서버에서는 일반적으로 CSRF 보호가 필요 없기 때문에 끄는 것이 일반적이다
		http.csrf(config -> config.disable());
		//로그인페이지 URL을 /api/member/login 지정하고, 인증되지 않은 사용자가 보호된 리소스를 요청하면 이URL로  
		//리다이렉트된다. 
		// ✅ [추가] 인증 없이 접근 가능한 경로 설정
	    http.authorizeHttpRequests(auth -> auth
	        //.requestMatchers("/", "/member/kakao", "/api/member/kakao").permitAll() // 메인, 로그인, 회원가입, 상품목록은 허용
	        //.requestMatchers("/error", "/login").permitAll()
	        .anyRequest().permitAll() // 임시 : 모두 허용
	    );
	    
		http.formLogin(config ->{  
		config.loginPage("/login");
		// 로그인 성공 시 실행될 핸들러 객체를 지정 코드 
		config.successHandler(new APILoginSuccessHandler());
		config.failureHandler(new APILoginFailHandler());
		}); 
	   
		// JWT 체크 추가 
		http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class); 

		//권한이 허가 되지 않았을 때 예외처리 메시지 처리 
		http.exceptionHandling(config  ->  { 
		config.accessDeniedHandler(new   CustomAccessDeniedHandler()); 
		}); 
		
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();

	    // 1. 허용할 도메인 설정 (패턴 대신 리스트로 명시)
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
	    
	    // 2. 허용할 HTTP 메서드
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
	    
	    // 3. 허용할 헤더 (Authorization 외에 모든 헤더를 허용하는 것이 속 편합니다)
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
	    
		// 웹소켓 연결 시 필요한 다양한 헤더를 수용하도록 "*"로 설정하거나 더 확장.
        configuration.setAllowedHeaders(Arrays.asList("*"));
	    
	    // 4. 자격 증명 허용
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	@Bean 
	public PasswordEncoder passwordEncoder() { 
	   return new BCryptPasswordEncoder(); 
	}
	
	@Bean
    public CommandLineRunner initData(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@honey.com";

            // 1. 이미 관리자 계정이 있는지 확인
            if (memberRepository.findById(adminEmail).isEmpty()) {
                // 2. 관리자 객체 생성 (사용 중인 Member 엔티티 구조에 맞게 수정하세요)
                Member admin = Member.builder()
                        .email(adminEmail)
                        .pw(passwordEncoder.encode("1111")) // 테스트용 비밀번호
                        .nickname("꿀템관리자")
                        .build();
                admin.addRole(MemberRole.MEMBER);
                admin.addRole(MemberRole.ADMIN);
                admin.changeStatus(1);
                
                // ✅ 기본 썸네일 파일명을 리스트에 명시적으로 추가
                admin.addImageString("default.jpg");

                memberRepository.save(admin);
                System.out.println("✅ 관리자 계정이 자동 생성되었습니다: " + adminEmail);
            }
        };
    }
}
