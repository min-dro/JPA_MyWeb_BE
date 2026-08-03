package com.kh.myweb.board.model.vo;

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

@Entity
@Table(name="REPLY")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Reply {

	@Id
	@Column(name="REPLY_NO")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int replyNo;
	
	@ManyToOne
	@JoinColumn(name="REF_BNO")
	private Board board;
	
	@ManyToOne
	@JoinColumn(name="REPLY_WRITER")
	private Member member;
	
	@Column(name="REPLY_CONTENT", length=400, nullable=false)
	private String replyContent;
	
	@Column(name="CREATE_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate;
	
	@Column(name="STATUS", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status;
	
}