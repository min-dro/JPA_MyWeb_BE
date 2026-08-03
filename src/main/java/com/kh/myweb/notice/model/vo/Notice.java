package com.kh.myweb.notice.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.myweb.member.model.vo.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// @Alias("notice")

@Entity
@Table(name="NOTICE")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Notice {

	@Id
	@Column(name="NOTICE_NO")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int noticeNo;				//	NOTICE_NO	NUMBER

	@Column(name="NOTICE_TITLE", nullable=false, length=35)
	private String noticeTitle;			//	NOTICE_TITLE	VARCHAR2(100 BYTE)
	
	// @Column(name="NOTICE_WRITER", length=30)
	// private String noticeWriter;	//	NOTICE_WRITER	VARCHAR2(30 BYTE)
	// > 우리가 DDL 로 직접 테이블을 생성했고, DDL 구문 내에서 직접 외래키도 미리 걸어줬었음!!
	//   이 경우에는 평소대로 쓰면된다!! 즉, @ManyToOne 을 안써도 된다!! (이미 테이블에 외래키가 걸려있으니까)
	
	// @ManyToOne 적용
	@ManyToOne
	@JoinColumn(name="NOTICE_WRITER")
	private Member member;
	
	@Column(name="NOTICE_CONTENT", nullable=false, length=200)
	private String noticeContent;		//	NOTICE_CONTENT	VARCHAR2(200 BYTE)
	
	@Column(name="CREATE_DATE", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate;			//	CREATE_DATE	DATE
	
	@Column(name="STATUS", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status;				//	STATUS	VARCHAR2(1 BYTE)
	
}