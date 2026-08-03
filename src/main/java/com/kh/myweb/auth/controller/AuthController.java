package com.kh.myweb.auth.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.myweb.auth.model.service.AuthService;
import com.kh.myweb.member.model.vo.Member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@CrossOrigin
@RestController
public class AuthController {

	// 비밀키를 전역 변수 겸 상수 필드로 빼기
	public static final String SECRET_KEY = "Hello123KHAcademy456Gangnam789WelcomeToHClass0";
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// 로그인(인증) 용 컨트롤러 - 어느 계정이든 로그인이 됨 (관리자든 일반사용자든)
	// > 범용으로 만들어서 일반 사용자 페이지의 로그인 구현 시에도 재활용 하면 된다!!
	@PostMapping("/auth/login")
	public ResponseEntity<String> loginMember(@RequestBody Member m) {
		
		// System.out.println(m);
		
		// 우선 아이디만 일치하는지를 검사
		Member loginUser = authService.loginMember(m.getUserId());
		
		// System.out.println(loginUser);
		// > 아이디가 일치하고 탈퇴하지 않은 경우에는 제대로 조회됨!!
		//   아이디가 일치하지 않거나 또는 탈퇴한 경우에는 null 로 나오게 된다.
		
		// m.getUserPwd() : 평문 비번
		// loginUser.getUserPwd() : 암호문 비번
		
		// 해당 회원의 정보가 존재하고 (loginUser != null) 비밀번호가 일치할 경우
		if((loginUser != null) &&
				(bCryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd()))) {
			// > 로그인 성공 처리
			
			/*
			 * * 기존의 로그인 구현 방법
			 * - 해당 회원의 전체 정보를 HttpSession 객체에 담아서 "전역" 으로 사용
			 * 
			 * > JSP 는 스프링 부트 프로젝트 내부에 존재하기 때문에
			 * 	 한번 HttpSession 객체에 데이터를 담으면 언제 어디서든지 다 꺼내 쓸 수 있었기 때문
			 *   (Controller 에서 담은 데이터를 JSP 에서도 꺼내 쓸 수 있었음)
			 *   
			 * > React 를 이용해서 프론트엔드를 별도로 분리해서 구현하게 되면
			 *   HttpSession 에 담긴 데이터를 전역으로 React 쪽에서는 꺼내서 쓸 수 없게 된다!!
			 *   (스프링 부트 프로젝트 외부에 존재하는 별도의 프로젝트이기 때문)
			 *   
			 * * 해결 방법 (React 프로젝트를 별도로 구현하였을 때 로그인 구현 방법)
			 * - 해당 회원의 정보를 응답데이터로 넘기고,
			 *   그 정보를 React 쪽에서 꺼내 쓸 수 있는 브라우저의 저장소
			 *   (기존 HttpSession 객체 역할을 대체하는 곳) 에 보관하기!!
			 * - 단, 회원의 정보를 그냥 응답데이터로 넘기기에는 보안 상 리스크가 있을 수 있으므로
			 *   "안전한 형태로 가공" 해서 응답데이터로 넘긴다!!
			 *   
			 * > 응답할 회원의 정보를 안전한 형태로 감싸주는 방법 : JWT 라는 개념을 이용
			 * 
			 * * JWT (Json Web Token)
			 * - JSON 형태 (자바스크립트의 객체 형태) 로
			 *   데이터를 안전하게 암호화 해서 담고, 데이터를 안전하게 전달할 수 있는 형태
			 * - 이 암호화된 데이터가 제대로된 데이터인지 검증해주는 역할,
			 *   이 암호화된 데이터의 위변조 여부도 판별해주는 역할 또한 겸한다!!
			 *   
			 * * JWT 구조
			 * 1. Header : 데이터를 담는 그릇의 "머리부"
			 * 			   데이터에 대한 전반적인 설정값들이 담겨있음
			 * 			   (암호화 시 사용한 알고리즘 종류 등)
			 * 2. Payload : 데이터를 담는 그릇의 "몸통부" (여기가 본론)
			 * 			    전달할 데이터의 실제 내용이 담겨있음
			 * 				(아이디 - PK, 권한, 발급일 등)
			 * 				단, 비밀번호값은 담으면 안됨!! (아무리 암호화가 되어있어도)
			 * 3. Signature : 서명
			 * 				  Header 와 Payload 값을 암호화한 정보를 합쳐놓은 부분
			 * 				  인가 기능에서 권한 등을 체크할 때 주로 사용
			 * 				  (제대호된 데이터인지 검증, 데이터의 위변조 여부 판별이 핵심 역할임)
			 * 
			 * => 만약 Signature 내용을 복호화해서 풀었는데
			 * 	  Payload 내용과 복호화한 Payload 내용이 다르다면, 위변조 된 것!!
			 * => 만약 Signature 내용을 Header 에 적힌 암호화 방식대로 복호화하려고 했는데
			 *    복호화가 안되면, Header 가 위변조 된 것!!
			 */
			
			// 위에서 조회해온 loginUser 값으로 JWT 토큰 생성
			
			// 스프링에서 JWT 기술을 이용하려면 우선 .jar (라이브러리) 연동을 먼저 해줘야 한다!! (pom.xml)
			// 1. 비밀 키 생성
			// > 최대한 복잡하고 남들이 유추할 수 없게 구성
			//	 최소 32글자 이상 (32byte == 256bit 이상), 특수기호 포함 X
			// > 전역 변수로 빼기 (Interceptor 에서 복호화할 때에도 써야하니까)
			//   보통은 전역 변수 보다는 별도의 설정파일로 담아뒀다가 동적 코딩 방식으로 불러다가 쓴다.
			
			// 2. 문자열 타입의 비밀 키를 서명을 위한 Key 형태의 객체로 가공 (JWT 라이브러리에서 제공)
			Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
			// > 문자열을 이루고 있는 글자들은 byte 단위로 하나씩 글자 하나하나로 쪼개서 (이 때 UTF-8 형식으로)
			//   byte 배열에 옮겨담은 후, 이걸 가지고 HMAC-SHA 알고리즘 종류 기반으로 Key 를 만들겠다.
			
			// 3. 위의 Key 를 가지고 JWT 토큰을 생성 (String 타입)
			String jwt = Jwts.builder() // Jwts 타입의 객체를 생성하겠다.
							 .setSubject(loginUser.getUserId()) // subject : 회원을 구분할 수 있는 고유값 (아이디, 회원번호 등)
							 .claim("name", loginUser.getUserName()) // 회원의 이름
							 .claim("role", loginUser.getRole()) // 이 회원의 권한 (관리자인지 일반회원인지)
							 .setIssuedAt(new Date()) // JWT 의 발급시간 (즉, 로그인한 시간)
							 .setExpiration(new Date(System.currentTimeMillis() + 1 * 60 * 60 * 1000)) // JWT 의 만료시간 (1시간)
							 .signWith(key, SignatureAlgorithm.HS256) // 서명 시 필요한 키, 서명 (암호화) 방법 지정
							 .compact(); // 위에서 설정한 내용들을 가지고 하나의 문자열 (토큰) 으로 만들겠다.
			
			// System.out.println(jwt);
			// > xxxxx.xxxxxxxxxxxx.xxxxxx
			// > . 을 기준으로 문자열이 "3등분" 되어있다!! (각각 Header.Payload.Signature 로 이루어짐)
			
			/*
			 * * jwt.io 접속 후 해당 내용을 해석해서 확인해보기!!
			 * - Header : 암호화 종류가 HS256
			 * - Payload : sub (아이디), name (회원이름), role (권한), iat (발급시간), exp (만료시간)
			 * - Signature : Header 및 Payload 내용의 암호화된 결과를 이어붙인것
			 */
			
			// 4. 이 만들어진 JWT 문자열을 응답데이터로 넘기시
			return ResponseEntity.status(HttpStatus.OK)
								 .body(jwt);
		
		} else {
			// > 로그인 실패 처리 - 응답데이터로 null 넘기기
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(null);
		}
	}
	
}