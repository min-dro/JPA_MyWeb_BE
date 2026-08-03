package com.kh.myweb.auth.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.myweb.member.model.vo.Member;

// Auth (인증, 인가) 기능을 위해 MEMBER 테이블과 CRUD 를 해야하므로
public interface AuthDao extends JpaRepository<Member, String> {

	// 로그인용 쿼리메소드
	// > SELECT * FROM MEMBER WHERE USER_ID = ? AND STATUS = 'Y'
	Member findByUserIdAndStatus(String userId, String status);

}