package com.kh.myweb.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kh.myweb.board.model.vo.Reply;

public interface ReplyDao extends JpaRepository<Reply, Integer> {

	// 댓글 조회용 쿼리메소드
	List<Reply> findByBoard_BoardNoAndStatusOrderByReplyNoDesc(int boardNo, String status);

	@Modifying
	@Query("""
			UPDATE Reply r
			   SET r.status = 'N'
			 WHERE r.replyNo = :replyNo
			   AND r.status = 'Y'
			""")
	int deleteReply(@Param("replyNo") int replyNo);

}