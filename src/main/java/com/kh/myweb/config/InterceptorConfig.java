package com.kh.myweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.myweb.common.interceptor.AuthInterceptor;

@Configuration // 이 클래스를 환경설정에서 용도로 쓰겠다.
public class InterceptorConfig implements WebMvcConfigurer {

	@Autowired
	private AuthInterceptor authInterceptor;
	// > preHandle 메소드가 호출될 수 있도록 AuthInterceptor 객체를 주입받는다.
	
	// 요청이 AuthInterceptor 를 거쳐가도록 연결해주기
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/members/**")
				.addPathPatterns("/notices/**")
				.addPathPatterns("/boards/**")
				.excludePathPatterns("/auth/login");
		// > "/members", "/notices", "/boards" 계역의 모든 요청은 (/**)
		//   이 AuthInterceptor 를 거쳐가고,
		//   유일하게 "/auth/login" 요청은 이 AuthInterceptor 를 피해가겠다.
		
		// > "/members", "/notices", "/boards" 계열의 요청은 아무나 못들어오게 막겠다.
		// > "블랙리스트"
		
		// "/auth/login" 계열의 요청은 아무나 들어올 수 있도록 풀겠다.
		// > "화이트리스트"
	}
	
}