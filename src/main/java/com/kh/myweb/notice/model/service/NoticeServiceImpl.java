package com.kh.myweb.notice.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.myweb.notice.model.dao.NoticeDao;
import com.kh.myweb.notice.model.vo.Notice;

@Service
public class NoticeServiceImpl implements NoticeService{

	@Autowired
	private NoticeDao noticeDao;

	@Transactional(readOnly=true)
	// > 조회용도의 Service 메소드 상단에도 @Transactional 을 붙일 수 있다!!
	//   단, readOnly=true 속성을 적어줘야 한다!! (조회 성능 최적화)
	@Override
	public List<Notice> selectNoticeList() {
		
		return noticeDao.findByStatusOrderByNoticeNoDesc("Y");
	}

	@Transactional(readOnly=true)
	@Override
	public Notice selectNotice(int noticeNo) {
		
		return noticeDao.findByNoticeNoAndStatus(noticeNo, "Y");
	}

	@Transactional
	@Override
	public Notice insertNotice(Notice n) {
		
		return noticeDao.save(n);
	}

	@Transactional
	@Override
	public Notice updateNotice(Notice n) {
		
		return noticeDao.save(n);
	}

	@Transactional
	@Override
	public int deleteNotice(int noticeNo) {
		
		return noticeDao.deleteNotice(noticeNo);
	}

}