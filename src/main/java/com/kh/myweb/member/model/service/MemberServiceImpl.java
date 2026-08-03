package com.kh.myweb.member.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.myweb.member.model.dao.MemberDao;
import com.kh.myweb.member.model.vo.Member;

/*
 * - 모든 팀원들이 동일한 MemberService 라는 인터페이스를 상속받아서 구현한다면
 * 	 같은 작업환경에서 본인이 맡은 메소드 코드만 채워서 작업하면 된다!!
 * - 인터페이스의 특징으로는 구현의 강제성을 준다 라는 점이고, 특히 같은 작업환경으로 맞출 때 유용하다!!
 * - 보통 인터페이스를 상속받아서 "구현 (implements)" 하는 클래스를 "구현체 클래스" 라고 부른다.
 * 	 "구현체 클래스" 의 이름은 보통 "인터페이스명Impl" 로 명명한다!!
 *
 * - 인터페이스 또는 추상클래스는 미완성된 상태이기 때문에 객체 생성이 불가하고,
 *   실체 구현체 클래스가 완성본이므로, 실체 객체 생성은 구현체 클래스로 한다!!
 * - 그래서 스프링에서 객체를 생성하기 위해 Bean 등록 또한 구현체 클래스 기준으로 Bean 으로 등록해준다.
 */
@Service // 구현체 클래스 상단에 어노테잇션을 이용해서 빈으로 등록한다!!
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberDao memberDao;

	@Override
	public List<Member> selectMemberList() {
		
		return memberDao.findByRoleOrderByEnrollDateAsc("USER");
	}

	@Override
	public Member selectMember(String userId) {
		
		return memberDao.findById(userId).orElse(null);
	}

	@Transactional
	@Override
	public Member updateMember(Member m) {
		
		return memberDao.save(m);
	}

	@Transactional
	@Override
	public int deleteMember(String userId) {
		
		return memberDao.deleteMember(userId);
	}
}