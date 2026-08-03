package com.kh.myweb.member.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.myweb.member.model.vo.Member;

public interface MemberDao extends JpaRepository<Member, String> {

	// 회원 목록 조회용 쿼리메소드
	// > SELECT * FROM MEMBER WHERE ROLE = 'USER' ORDER BY ENROLL_DATE ASC
	List<Member> findByRoleOrderByEnrollDateAsc(String role);
	
	// 회원 탈퇴용 JPQL (소프트 삭제, STATUS 를 'N'로)
	// > UPDATE MEMBER SET STATUS = 'N', MODIFY_DATE = CURRENT_TIMESTAMP WHERE USER_ID = ?
	@Modifying
	@Query("""
				UPDATE Member m
				   SET m.status = 'N'
				     , m.modifyDate = CURRENT_TIMESTAMP
				 WHERE m.userId = :userId
			""")
	int deleteMember(@Param("userId") String userId);
	
}