package com.kh.myweb.member.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// @Alias("member")
// > 지금부터 이 클래스의 별칭은 "member" 임을 알려주는 MyBatis 어노테이션

@Entity
@Table(name="MEMBER")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Member {

	// 필드부
	@Id
	@Column(name="USER_ID", length=30)
	private String userId;			//	USER_ID	VARCHAR2(30 BYTE)
	
	@Column(name="USER_PWD", nullable=false, length=100)
	private String userPwd;			//	USER_PWD	VARCHAR2(100 BYTE)
	
	@Column(name="USER_NAME", nullable=false, length=5)
	private String userName;		//	USER_NME	VARCHAR2(15 BYTE)
	
	@Column(name="EMAIL", length=100)
	private String email;			//	EMAIL	VARCHAR2(100 BYTE)
	
	@Column(name="GENDER", columnDefinition="CHAR(1)")
	private String gender;			//	GENDER	VARCHAR2(1 BYTE)
	
	@Column(name="AGE")
	private int age;				//	AGE	NUMBER
	
	@Column(name="PHONE", length=13)
	private String phone;			//	PHONE	VARCHAR2(13 BYTE)
	
	@Column(name="ADDRESS", length=35)
	private String address;			//	ADDRESS	VARCHAR2(100 BYTE)
	
	@Column(name="ENROLL_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime enrollDate;		//	ENROLL_DATE	DATE
	
	@Column(name="MODIFY_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime modifyDate;		//	MODIFY_DATE	DATE
	
	@Column(name="STATUS", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status;			//	STATUS	VARCHAR2(1 BYTE)
	
	@Column(name="ROLE", columnDefinition="VARCHAR(6) DEFAULT 'USER'")
	private String role;
}