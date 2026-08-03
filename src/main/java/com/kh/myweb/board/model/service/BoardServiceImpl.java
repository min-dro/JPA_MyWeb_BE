package com.kh.myweb.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.myweb.board.model.dao.BoardDao;
import com.kh.myweb.board.model.dao.ReplyDao;
import com.kh.myweb.board.model.vo.Board;
import com.kh.myweb.board.model.vo.Reply;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDao boardDao;
	
	@Autowired
	private ReplyDao replyDao;

	@Override
	public Page<Board> selectBoardList(Pageable pageable) {
		
		return boardDao.findByStatusOrderByBoardNoDesc("Y", pageable);
	}

	@Override
	public Page<Board> selectSearchList(String keyword, Pageable pageable) {
		
		return boardDao.findByBoardTitleContainingAndStatusOrderByBoardNoDesc(keyword, "Y", pageable);
	}

	@Transactional
	@Override
	public Board insertBoard(Board b) {
		
		return boardDao.save(b);
	}

	@Transactional
	@Override
	public int increaseCount(int boardNo) {
		
		return boardDao.increaseCount(boardNo);
	}

	@Override
	public Board selectBoard(int boardNo) {
		
		return boardDao.findById(boardNo).orElse(null);
		// > 이미 조회수 증가에서 STATUS 가 'Y' 인 조건을 제시했기 때문에
		//   그냥 findById 로 조회해와도 된다!!
	}

	@Transactional
	@Override
	public Board updateBoard(Board b) {
		
		return boardDao.save(b);
	}

	@Transactional
	@Override
	public int deleteBoard(int boardNo) {
		
		return boardDao.deleteBoard(boardNo);
	}

	@Override
	public List<Reply> selectReplyList(int boardNo) {
		
		return replyDao.findByBoard_BoardNoAndStatusOrderByReplyNoDesc(boardNo, "Y");
	}

	@Transactional
	@Override
	public Reply insertReply(Reply r) {
		
		return replyDao.save(r);
	}

	@Transactional
	@Override
	public Reply updateReply(Reply r) {
		
		return replyDao.save(r);
	}

	@Transactional
	@Override
	public int deleteReply(int replyNo) {
		
		return replyDao.deleteReply(replyNo);
	}
	
	
}