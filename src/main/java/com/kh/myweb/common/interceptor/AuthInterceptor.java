package com.kh.myweb.common.interceptor;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.myweb.auth.controller.AuthController;
import com.kh.myweb.auth.model.service.AuthService;
import com.kh.myweb.member.model.vo.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 로그인 요청을 제외한 나머지 모든 요청 시 "관리자가 맞냐" 검사해주는 인터셉터
@Component // 이 클래스를 빈으로 등록하겠다.
public class AuthInterceptor implements HandlerInterceptor {

	// AuthService 도 의존성 주입으로 객체를 받아내야함
	@Autowired
	private AuthService authService;
	// > Interceptor 에서 DB 까지 다녀와야 한다면 Service 부터 호출해서 시작하면 된다!!
	
	// 요청 "전" 에 "관리자가 맞는지" 검증하겠다. (jwt 티켓 검사용 메소드)
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		   throws Exception {
		
		// System.out.println("잘 호출되나?");
		
		// 우선 요청 시 전달했었던 인증 토큰 정보 (jwt 티켓) 를 URL 주소의 Header 영역으로부터 뽑아내기
		
		// request.getParameter("키값") : String(밸류값)
		// request.getParameterValues("키값") : String[](밸류값들)
		// > 요청 시 전달값들을 request 객체의 parameter 영역으로부터 뽑아내는 구문들
		
		// request.getHeader("키값") : String(설정밸류값)
		// > 요청 시 설정값들을 request 객체의 header 영역으로부터 뽑아내는 구문
		String authHeader = request.getHeader("Authorization");
		
		// System.out.println(authHeader);
		// > 토큰이 전달된 경우 "Bearer xxxxxx.xxxxxxxxx.xxxxx"
		//   토큰이 전달되지 않을 경우 null
		
		// 우선 앞의 "Bearer " 라는 접두어를 제거
		if((authHeader != null) &&
				(!authHeader.equals("")) &&
				(authHeader.startsWith("Bearer"))) {
			// > 문자열.startsWith("시작문자열");
			//   문자열이 해당 시작문자열로 시작할 경우 true 를 반환함 (접두어 검사 시 용이)
			
			String jwtTokenString = authHeader.substring(7);
			
			// System.out.println(jwtTokenString);
			// 제대로된 토큰이 전달된 경우 "xxxxxx.xxxxxxxxx.xxxxx"
			
			// 이 추출해낸 문자열 형태의 jwt 를 가지고 대조 작업 진행
			
			// 1. JWT 발급 시 썼었던 비밀 키를 똑같은 형태의 Key 객체로 만들기
			Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
			// > 발급 시 암호화했었을 때 썼던 키를 검증 시 복호화할 때에도 똑같이 쓴다!!
			//   "양방향" 암호화 방식 : 암호화 + 복호화 둘 다 가능한 방식
			//   그 중에서도 암호화, 복호화 둘 다 가능한 방식

			// 2. 위에서 얻어낸 Key 객체를 통해 jwt 문자열을 풀어줌 (파싱)
			JwtParser parser = Jwts.parserBuilder()
							       .setSigningKey(key)
							       .build();
			
			Claims claims = parser.parseClaimsJws(jwtTokenString).getBody();
			// > claims 객체에 Payload 에 담겨있던 데이터들이 담겨있음!!
			
			// 3. claims 에 담긴 데이터를 검증해보기
			// > 우선 만료시간 먼저 검사할 것!!
			if(!claims.getExpiration().before(new Date())) {
				// > claims.getExpiration() : JWT 정보 중에 "만료시간" 을 꺼내주는 메소드
				// > 만료시간.before(현재시간) : 만료시간이 현재시간보다 이전입니까? - 만료됨
				//	 !만료시간.before(현재시간) : 만료시간이 현재시간보다 이전이 아닙니까? 즉, 이후입니까? - 유효함
				
				// > 이 시점 기준으로 만료기산 기준으로 유효한 토큰임!!
				// > claims 에 담겨있는 고유한 회원의 아이디값 (subject) 추출
				String userId = claims.getSubject();
				
				// 아이디가 제대로된 회원의 아이디인지를 검사
				// > DB 로 부터 회원 아이디로 해당 회원의 정보를 조회해오기 (기존 로그인 서비스 재활용)
				if(userId != null) {
					
					Member m = authService.loginMember(userId);
					
					if((m != null) &&
							(userId.equals(m.getUserId())) &&
							(m.getRole().equals("ADMIN"))) { // role 검사 추가 (인가의 핵심)
						// > 유효한 회원이라면 - Controller 로 들어가도 됨
						
						// > 이 시점 기준으로 만료시간, 아이디, 권한 체크가 완료되었음!! (최소한으로 검사해야할 항목들)
						
						return true;
						// > Controller 로 요청이 흘러가게끔
					}	
				}
			}
		}
		
		// 그 외의 다른 모든 경우의 수들은 Controller 로 요청이 못들어가게 막는다.
		return false;
	}
	
}
/*
 * * 인가 기능 구현의 원리
 * - 인증 기능 시 발급했었던 JWT 토큰 내용을 하나하나씩 검증해주면 됨!!
 * - 특히, 계정 정보 중에서도 ROLE (권한이 맞아 떨어져야함
 * 
 * * 인증, 인가의 차이점
 * - 인증은 아이디와 비번만 맞으면 무조건 통과
 * - 인가는 그 중에서도 권한이 맞아 떨어져야함 
 * 
 * * 실무에서는 우라가 만든 방식보다 훨씬 더 복잡하게 인증, 인가 기능을 구현한다!!
 * - 인증 : 실제 구현 시에는 JWT 토큰을 두개를 이용한다.
 * 		   Access Token : 우리가 만든 토큰 (입장 티켓)
 * 						  우리는 만료시간을 1시간으로 지정해뒀지만, 실무에서는 5-10분으로 지정해준다.
 * 						  우리는 sessionStorage 에 담아뒀지만, 실무에서는 리엑트의 State 변수에 전역으로 저장해준다.
 * 		   Refresh Token : 그 만료시간마다 Access Token 을 다시 발급해주는 새로고침용의 토큰
 * 						   Refresh Token 은 보통 Cookie 에 저장한다.
 * 						   우리는 만료시간을 1시간으로 적당히 지정하고, Refresh Token 을 만들지 않았음!!
 *						   리액트의 State 변수는 새로고침 시 초기화되서 로그인이 출리는 이슈가 발생한다!!
 *						   (새로고침 시 로그인 풀림 방지를 위해 Refresh Token을 같이 쓴다)
 *						   우리는 sessionStorage 에 담아뒀기 때문에 새로고침 시 로그인이 풀리지 않는다!!
 * - 인가 : 우리처럼 직접 인터셉터를 이용해서 검증하는 구문을 짜서 쓰기도 하고,
 * 		   Spring Security 모듈에서 제공하는 검증해주는 라이브러리 코드를 쓰기도 한다!!
 * 		   (내부적으로 검증 원리는 우리가 짠거랑 거의 비슷함!!)
 */