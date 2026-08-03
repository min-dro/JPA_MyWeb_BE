package com.kh.myweb.member.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.myweb.member.model.service.MemberService;
import com.kh.myweb.member.model.vo.Member;

@CrossOrigin
@RestController
public class MemberController {

	@Autowired
	private MemberService memberService /* = new MemberServiceImpl() */;
	// > 내부적으로 실제 객체 생성은 구현체 클래스 기준으로 일어난다!!
	//   (인터페이스로는 애초에 객체 생성이 불가하다)
	// > 자식타입인 구현체 클래스 타입으로 객체가 생성된 뒤 "다형성" 에 의해 부모타입인
	//   인터페이스 타입의 변수에 담기는 꼴임!! (의존성 주입, DI)
	
	// 회원 목록 조회용 컨트롤러
	@GetMapping("/members")
	public ResponseEntity<ArrayList<Member>> selectMemberList() {
		
		// System.out.println("잘 호출되나?");
		
		// 서비스 호출
		ArrayList<Member> list = (ArrayList)memberService.selectMemberList();
		
		/*
		for(Member m : list) {
			
			System.out.println(m);
		}
		*/
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
	}

	// 회원 상세 조회용 컨트롤러
	@GetMapping("/members/{userId}")
	public ResponseEntity<Member> selectMember(@PathVariable String userId) {
		
		// System.out.println(userId);
		
		// 서비스 호출
		Member m = memberService.selectMember(userId);
		
		// System.out.println(m);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(m);
	}

	// 회원 정보 수정용 컨트롤러
	@PutMapping("/members/{userId}")
	public ResponseEntity<String> updateMember(@PathVariable String userId,
											   @RequestBody Member m) {
		
		// System.out.println(userId);
		// System.out.println(m);
		
		 // 회원정보수정일 필드값을 현재시간으로 셋팅하기
		m.setModifyDate(LocalDateTime.now());
		// > LocalDateTime.now() : MySQL 의 CURRENT_TIMESTAMP 역할임
		
		if(m.getGender().equals("")) {
			
			m.setGender(null);
		}
		// > MySQL 에서는 "" 이 자동으로 NULL 로 들어가지 않음!! (오라클에서는 됐었음)
		// > 이 코드를 살려두더라도 오라클레서는 상관 없이 잘 돌아갈 것
		
		// 서비스 호출
		Member updateMemb = memberService.updateMember(m);
		// > JpaRepository 에서 제공되는 save 메소드는 INSERT / UPDATE 성공 시 추가 또는 수정된 행의 정보를
		//   VO로 리턴해주고, 실패 시 null 을 리턴해준다!!
		
		String message = (updateMemb != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}

	// 회원 탈퇴용 컨트롤러
	@DeleteMapping("/members/{userId}")
	public ResponseEntity<String> deleteMember(@PathVariable String userId) {
		
		// 서비스 호출
		int result = memberService.deleteMember(userId);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
		// > "success" 일 경우는 OK 가 아닌 NO_CONTENT (204) 로 응답해도 된다!!
	}

}