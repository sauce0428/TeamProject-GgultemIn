package com.honey.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
    /**
     * 메시지 브로커(Message Broker) 설정을 구성
     * 클라이언트가 메시지를 보낼 때와 받을 때의 경로 규칙 정의
     */
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. 서버에서 클라이언트로 메시지를 보낼 때 사용할 prefix 설정
        // 클라이언트가 "/topic/..." 경로를 구독(subscribe)하고 있으면 메시지를 받을 수 있도록설정.
        // 보통 일대다(Pub/Sub) 통신에 사용됨.
        config.enableSimpleBroker("/topic");

        // 2. 클라이언트가 서버로 메시지를 보낼 때(발행) 사용할 prefix 설정
        //리액트에서 메시지를 보낼 때 주소 앞에 "/app"을 붙여야 @MessageMapping 컨트롤러로 전달된다.
        //리액트에서 "/app/chat"으로 보내면 서버의 @MessageMapping("/chat")이 실행됨.
        config.setApplicationDestinationPrefixes("/app");
    }
	
	/**
     * 클라이언트가 WebSocket 서버에 처음 연결할 접속 지점(Endpoint)을 등록.
     */
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 리액트 new SockJS('http://localhost:8080/ws')로 연결을 시도하는 지점.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 모든 도메인에서의 접속을 허용 (CORS 해결)
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저를 위해 SockJS 폴백 기능을 활성화.
    }
	
	/**
     * 일반적인 HTTP 통신에 대한 CORS(Cross-Origin Resource Sharing) 설정을 정의.
     * 리액트와 백엔드의 포트가 다를 때 발생하는 보안 차단을 해제한다.
     */
    /*  이부분은 CustomSecurityConfig: CorsConfigurationSource라는 Bean을 생성하여 스프링 시큐리티 필터 체인에 직접 주입하고 있다. 이는 HTTP 요청(REST API)에 대한 보안 검사 단계에서 CORS를 제어한다. WebSocketConfig: CorsFilter라는 별도의 Bean을 생성하여 일반적인 필터로 등록하고 있어서 중복현상이 일어난다. 그래서 주석처리한다. */
    /*
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키나 인증 정보를 포함한 요청을 허용.
        config.addAllowedOriginPattern("*"); // 모든 출처(Origin)의 접근을 허용.
        config.addAllowedHeader("*"); // 모든 헤더(Header) 정보를 허용.
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모든 HTTP 메서드를 허용.
        // 모든 경로("/**")에 대해 위에서 설정한 CORS 규칙을 적용.
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
*/




}
