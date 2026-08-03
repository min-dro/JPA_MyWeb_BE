package com.kh.myweb.notice.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.myweb.auth.controller.AuthController;
import com.kh.myweb.common.template.XssDefencePolicy;
import com.kh.myweb.member.model.service.MemberService;
import com.kh.myweb.member.model.vo.Member;
import com.kh.myweb.notice.model.service.NoticeService;
import com.kh.myweb.notice.model.vo.Notice;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private MemberService memberService;
	
	// 공지사항 목록 조회용 컨트롤러
	@GetMapping("/notices")
	public ResponseEntity<ArrayList<Notice>> selectNoticeList() {
		
		// 서비스 호출
		ArrayList<Notice> list = (ArrayList)noticeService.selectNoticeList();
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
	}
	
	// 공지사항 작성용 컨트롤러
	@PostMapping("/notices")
	public ResponseEntity<String> insertNotice(@RequestBody Notice n,
											   HttpServletRequest request) {
		
		// System.out.println(n);
		// > 작성자의 아이디가 안들어가있음!!
		
		// request 객체를 매개변수로 추가했고, 여기서부터 현재 로그인한 회원의 정보를 알아내기!!
		// URL 요청의 Header 에 Authorization 으로 "Bearer xxxxx.xxxxxxx.xxxx" 를 넣어뒀음
		// > 기존 인터셉터의 검증용 코드와 동일
		String authHeader = request.getHeader("Authorization");
		
		// > 인터셉터에서 이미 검증을 거쳐왔기 떄문에 중첩 if문 사용 X
		//   그냥 바로 뽑아서 쓰면 된다!!
		String jwtTokenString = authHeader.substring(7);
		// > "xxxxx.xxxxxxx.xxxx"
		
		// 위의 jwtTokenString 을 파싱하기
		Key key = Keys.hmacShaKeyFor(
						AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		String userId = claims.getSubject();
		
		// System.out.println(userId);
		
		// 이 아이디값을 가지고 MemberService 를 호출해서 해당 회원의 정보를 Member 객체로 받아줘야함
		Member m = memberService.selectMember(userId);
		
		// System.out.println(m);
		
		// n 의 member 필드에 m 을 셋팅 (setter)
		n.setMember(m);
		
		// 현재시간이 담겨있는 컬럼에 not null 제약조건이 걸려있다면?
		n.setCreateDate(LocalDateTime.now());
		
		// XSS 공격 방지 처리 - 공통 코드 작업 참고
		n.setNoticeTitle(XssDefencePolicy.defence(n.getNoticeTitle()));
		n.setNoticeContent(XssDefencePolicy.defence(n.getNoticeContent()));
		Notice insertNo = noticeService.insertNotice(n);
		String message = (insertNo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 공지사항 상세조회용 컨트롤러
	@GetMapping("/notices/{noticeNo}")
	public ResponseEntity<Notice> selectNotice(@PathVariable int noticeNo) {
		
		// System.out.println(noticeNo);
		
		Notice n = noticeService.selectNotice(noticeNo);
		
		// System.out.println(n);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(n);
	}
	
	// 공지사항 수정용 컨트롤러
	@PutMapping("/notices/{noticeNo}")
	public ResponseEntity<String> updateNotice(@PathVariable int noticeNo,
											   @RequestBody Notice n) {
		
		// System.out.println(noticeNo);
		// System.out.println(n);
		n.setNoticeTitle(XssDefencePolicy.defence(n.getNoticeTitle()));
		n.setNoticeContent(XssDefencePolicy.defence(n.getNoticeContent()));
		Notice updateNo = noticeService.updateNotice(n);
		System.out.println(updateNo);
		
		String message = (updateNo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 공지사항 삭제용 컨트롤러
	@DeleteMapping("/notices/{noticeNo}")
	public ResponseEntity<String> deleteNotice(@PathVariable int noticeNo) {
		
		// 서비스 호출
		int result = noticeService.deleteNotice(noticeNo);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
}