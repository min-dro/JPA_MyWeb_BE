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

// @Alias("board")

@Entity
@Table(name="BOARD")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Board {

	@Id
	@Column(name="BOARD_NO")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int boardNo;			//	BOARD_NO	NUMBER
	
	@Column(name="BOARD_TITLE", length=255, nullable=false)
	private String boardTitle;		//	BOARD_TITLE	VARCHAR2(100 BYTE)
	
	// @Column(name="BOARD_WRITER", length=30, nullable=false)
	// private String boardWriter;		//	BOARD_WRITER	VARCHAR2(4000 BYTE)
	
	// @ManyToOne 적용
	@ManyToOne
	@JoinColumn(name="BOARD_WRITER")
	private Member member;
	
	@Column(name="BOARD_CONTENT", length=255, nullable=false)
	private String boardContent;	//	BOARD_CONTENT	VARCHAR2(4000 BYTE)
	
	@Column(name="ORIGIN_NAME", length=255)
	private String originName;		//	ORIGIN_NAME	VARCHAR2(100 BYTE)
	
	@Column(name="CHANGE_NAME", length=255)
	private String changeName;		//	CHANGE_NAME	VARCHAR2(100 BYTE)
	// > 반약, 게시글 1개 당 첨부파일이 여러개 들어갈 수 있다면? BOARD 따로 ATTACHMENT 따로 만들어야함!!
	//   자식 Entity 인 Attachment 쪽에서 private Board board; 로 @ManyToOne 관계를 지정하면 된다!!
	
	@Column(name="COUNT", columnDefinition="INT DEFAULT 0")
	private int count;				//	COUNT	NUMBER
	
	@Column(name="CREATE_DATE", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createDate;		//	CREATE_DATE	DATE
	
	@Column(name="STATUS", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status;			//	STATUS	VARCHAR2(1 BYTE)
	
}