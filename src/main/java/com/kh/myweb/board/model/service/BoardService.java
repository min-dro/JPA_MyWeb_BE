package com.kh.myweb.board.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kh.myweb.board.model.vo.Board;
import com.kh.myweb.board.model.vo.Reply;

public interface BoardService {

	// 실제 게시글 목록을 조회해오는 서비스
	Page<Board> selectBoardList(Pageable pageable);
	
	// 실제 검색된 게시글의 목록을 조회해오는 서비스
	Page<Board> selectSearchList(String keyword, Pageable pageable);
	
	// 게시글 작성용 서비스
	Board insertBoard(Board b);
	
	// 게시글 상세조회용 서비스
	// 조회수 증가용 서비스
	int increaseCount(int boardNo);
	
	// 실제 게시글 하나만 조회해주는 서비스 
	Board selectBoard(int boardNo);
	
	// 게시글 수정용 서비스
	Board updateBoard(Board b);
	
	// 게시글 삭제용 서비스
	int deleteBoard(int boardNo);
	
	// 댓글 조회용 서비스
	List<Reply> selectReplyList(int boardNo);
	
	// 댓글 작성용 서비스
	Reply insertReply(Reply r);
	
	// 댓글 수정용 서비스
	Reply updateReply(Reply r);
	
	// 댓글 삭제용 서비스
	int deleteReply(int replyNo);
	
}