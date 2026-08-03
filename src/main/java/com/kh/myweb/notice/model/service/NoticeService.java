package com.kh.myweb.notice.model.service;

import java.util.List;

import com.kh.myweb.notice.model.vo.Notice;

public interface NoticeService {

	// 공지사항 목록 조회용 서비스
	List<Notice> selectNoticeList();
	
	// 공지사항 상세 조회용 서비스
	Notice selectNotice(int noticeNo);
	
	// 공지사항 작성용 서비스
	Notice insertNotice(Notice n);

	// 공지사항 수정용 서비스
	Notice updateNotice(Notice n);
	
	// 공지사항 삭제용 서비스
	int deleteNotice(int noticeNo);
}