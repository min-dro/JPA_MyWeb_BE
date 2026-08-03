package com.kh.myweb.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kh.myweb.notice.model.vo.Notice;

public interface NoticeDao extends JpaRepository<Notice, Integer>{

	// 공지사항 목록 조회용 쿼리메소드 (STATUS = 'Y', 최신순 정렬)
	// > SELECT * FROM NOTICE WHERE STATUS = 'Y' ORDER BY NOTICE_NO DESC
	List<Notice> findByStatusOrderByNoticeNoDesc(String status);
	
	// 공지사항 상세조회용 쿼리메소드
	// > SELECT * FROM NOTICE WHERE NOTICE_NO = ? AND STATUS = 'Y'
	Notice findByNoticeNoAndStatus(int noticeNo, String status);
	
	// 공지사항 삭제용 JPQL (소프트삭제)
	@Modifying
	@Query("""
				UPDATE Notice n
				   SET n.status = 'N'
				 WHERE n.noticeNo = :noticeNo
				   AND n.status = 'Y'
			""")
	int deleteNotice(@Param("noticeNo") int noticeNo);
	
}