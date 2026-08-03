package com.kh.myweb.board.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.apache.ibatis.annotations.Param;

import com.kh.myweb.board.model.vo.Board;

public interface BoardDao extends JpaRepository<Board, Integer> {

	// 게시글 목록조회용 쿼리메소드
	// > SELECT * FROM BOARD WHERE STATUS = 'Y' ORDER BY BOARD_NO DESC
	Page<Board> findByStatusOrderByBoardNoDesc(String status, Pageable pageable);
	// > 전체 조회용 쿼리메소드로 정의하되,
	// > Pageable 을 같이 전달한다!! (알아서 페이지 구간별로 조회할 수 있도록)
	// > 이 때, 반환형은 Page 가 된다!!
	
	// 게시글 검색용 쿼리메소드
	// > SELECT * FROM BOARD
	//   WHERE BOARD_TITLE LIKE '%' || ? || '%' AND STATUS = 'Y'
	//	 ORDER BY BOARD_NO DESC
	Page<Board> findByBoardTitleContainingAndStatusOrderByBoardNoDesc(String keyword, String status, Pageable pageable);
	
	// 게시글 상세조회용 조회수 증가용 JPQL
	@Modifying
	@Query("""
				UPDATE Board b
				   SET b.count = b.count + 1
				 WHERE b.boardNo = :boardNo
				   AND b.status = 'Y'
			""")
	int increaseCount(@Param("boardNo") int boardNo);
	
	// 게시글 삭재용 JPQL (소프트 삭제)
	@Modifying
	@Query("""
				UPDATE Board b
				   SET b.status = 'N'
				 WHERE b.boardNo = :boardNo
				   AND b.status = 'Y'
			""")
	int deleteBoard(@Param("boardNo") int boardNo);

}