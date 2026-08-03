package com.kh.myweb.auth.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.myweb.auth.model.dao.AuthDao;
import com.kh.myweb.member.model.vo.Member;

@Service
public class AuthService {

	@Autowired
	private AuthDao authDao;
	
	public Member loginMember(String userId) {
		
		return authDao.findByUserIdAndStatus(userId, "Y");
	}
	
}