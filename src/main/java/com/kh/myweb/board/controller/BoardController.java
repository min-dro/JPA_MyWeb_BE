package com.kh.myweb.board.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.myweb.auth.controller.AuthController;
import com.kh.myweb.board.model.service.BoardService;
import com.kh.myweb.board.model.vo.Board;
import com.kh.myweb.board.model.vo.Reply;
import com.kh.myweb.common.model.vo.PageInfo;
import com.kh.myweb.common.template.FileRenamePolicy;
import com.kh.myweb.common.template.Pagination;
import com.kh.myweb.member.model.service.MemberService;
import com.kh.myweb.member.model.vo.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private MemberService memberService;
	
	// 일반게시글 목록 조회용 컨트롤러
	@GetMapping("/boards")
	public ResponseEntity<HashMap<String, Object>> selectBoardList(
				@RequestParam(value="cpage", defaultValue="1") int currentPage) {
		
		/*
		 * * JPA 에서의 페이징처리
		 * 
		 * - 기존 MyBatis 에서는 페이징 처리를 위해 아래의 과정을 직접 구현했었음
		 * 1) 총 게시글 수 조회 쿼리를 따로 작성 (listCoint)
		 * 2) PageInfo 객체에 7개의 변수를 직접 계산해서 셋팅
		 * 3) Pagination 을 통해 최대한 공통코드 작업을 해서 계산과정을 줄임
		 * 4) 인라인뷰를 여러겹 쓰거나 또는 MyBatis 의 RowBounds 객체를 통해 페이지 구간별로 잘라서 조회
		 * 
		 * - JPA 에서도 페이징 처리를 위한 객체들 (Pageable, Page<Entity타입>) 을 제공해준다!!
		 * 1) Pageable : 몇 페이지의 몇 개를 조회할지 정보를 담는 객체
		 * 	  [ 표현법 ]
		 * 	  Pageable pageable = PageRequest.of(페이지번호, 한체이지당조회할갯수);
		 * 	  > 단, 페이지번호는 0부터 시작한다!! (즉, 1번페이지가 == 0)
		 *    > MyBatis 의 RowBounds 와 비슷한 개념임 (앞에서부터 몇 개를 건너뛰고 몇 개를 조회할지)
		 * 
		 * 2) Page<Entity> : 조회 결과 + 페이징 정보가 함께 담겨있는 객체
		 * 	  > List<Entity> 와 비슷, Page 객체에서 제공하는 다양한 메소드들을 통해 페이징 정보를 알아낼 수 있음
		 * 
		 * 	  [ 표현법 ]
		 * 	  Page<Entity> page 가 있다고 치면
		 *	  page.getContent() : 조회된 데이터 목록 (List<Entity>)
		 *	  page.getTotalElements() : 총 게시글의 갯수 (listCount값 : COUNT 쿼리를 자동 실행)
		 *	  page.getTotalPages() : 총 페이지 수 (maxPage)
		 *	  page.getNumber() : 현재 페이지 번호 (0부터 시작, currentPage - 1)
		 * 	  page.getSize() : 한 페이지 당 갯수 (boardLimit)
		 * 
		 * - Pageable 과 Page<Entity> 의 내부 동작 원리 
		 *   : 아예 처음부터 쿼리문을 통해 몇본 페이지의 몇 개를 조회해오는 개념임!!
		 *     DB 로 부터 처음부터 필요한 데이터만 뽑아옴!!
		 *     
		 * - 기존 MyBatis 의 RowBounds 객체의 동작 원리
		 * 	 : 일단 모든 것들을 다 조회 후 내부적으로 (자바 코드를 통해) 필요한 구간의 데이터만 끊어냄!!
		 *     성능 상 쓰지 않는 것이 권장사항!!
		 */
		
		// 사용자가 요청한 currentPage 는 매개변수로 받아온 상태!!
		// + boardLimit(한 페이지당 몇개씩 보여질건지), pageLimit(페이징바 숫자 갯수) 만 마저 셋팅
		int boardLimit = 5;
		int pageLimit = 5;
		
		// > 위의 값들을 가지고 Pageable 객체를 먼저 셋팅
		Pageable pageable = PageRequest.of(currentPage - 1, 5);
		
		// > 위에서 셋팅한 Pageable 객체를 넘기면서 실제 목록을 조회해오기 (구간별로)
		//   이 때, 조회된 결과는 Page 객체로 넘어온다.
		Page<Board> page = boardService.selectBoardList(pageable);
		
		// > Page 객체로부터 조회된 리스트, 총 게시글의 갯수 구해보기
		List<Board> list = page.getContent();
		
		long listCount = page.getTotalElements();
		// > 내부적으로 COUNT 함수를 실행해서 갯수를 세오는 것!!
		
		// > PageInfo 객체 생성하기
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage,
													pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("list", list); // 실제 목록 (tbody) 에 출력 용도
		hm.put("pi", pi); // 페이징바 만들어낼 용도
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}
	
	// 일반게시글 검색용 컨트롤러
	@GetMapping("/boards/search")
	public ResponseEntity<HashMap<String, Object>> searchBoardList(
			@RequestParam(value="cpage", defaultValue="1") int currentPage,
			String keyword) {
		
		// 검색 목록 조회 또한 페이징 처리가 적용되야 함!!
		
		int pageLimit = 5;
		int boardLimit = 5;
		
		// > Pageable 객체 셋팅
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
		// > Pageable 을 넘기면서 조회헤오기
		Page<Board> page = boardService.selectSearchList(keyword, pageable);
		
		// > 각각의 응답데이터 셋팅하기
		List<Board> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)searchCount, currentPage, pageLimit, boardLimit);
		
		// pi 와 list 를 HashMap 으로 묶어서 하나의 응답데이터로 넘길 것!!
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}
	
	// 일반게시글 작성용 컨트롤러
	@PostMapping("/boards")
	public ResponseEntity<String> insertBoard(Board b, MultipartFile upfile,
										   			   HttpSession session,
										   			   HttpServletRequest request) {
	
		// 작성자 (로그인한 회원) 정보 뽑기
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY
										.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		String userId = claims.getSubject();
		
		Member m = memberService.selectMember(userId);
		
		b.setMember(m);
		
		// System.out.println(b);
		// > multipart/form-data 형식으로 요청 시에는
		//   POST 방식으로 요청을 보냈더라도 @RequestBody 어노테이션을 쓰지 않는다!!
		// > @ModelAttribute 어노테이션을 이용한다!! (이 마저도 생략 가능)
		
		// System.out.println(upfile);
		// > 넘어온 첨부파일이 없을 경우는 null / 있을 경우에는 주소값 출력
		
		// 넘어온 첨부파일이 있을 경우
		// > 파일명 수정작업 후 서버로 업로드 (공통코드), originName, changeName 필드값을 셋팅
		if(upfile != null) {
			
			// System.out.println(upfile.getOriginalFilename());
			
			String originName = upfile.getOriginalFilename();
			
			String changeName
						= FileRenamePolicy.saveFile(upfile, session,
													"/resources/board_upfiles/");
			
			b.setOriginName(originName);
			b.setChangeName(changeName);
		}
		
		// 이 시점 기준으로 넘어온 첨부파일이 있다면 b 의 originName, changeName 필드값이 존재
		// 이 시점 기준으로 넘어온 첨부파일이 없다면 b 의 originName, changeName 필드값은 null
		
		b.setStatus("Y");
		
		// 서비스 호출
		Board insertBo = boardService.insertBoard(b);
		
		String message = (insertBo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 일반게시글 상세조회용 컨트롤러
	@GetMapping("/boards/{boardNo}")
	public ResponseEntity<Board> selectBoard(@PathVariable int boardNo) {
		
		// 우선 조회수 증가부터 하고 돌아와야함
		int result = boardService.increaseCount(boardNo);
		
		// 조회수 증가에 성공했다면 그제서야 단일행 조회를 다녀오는것!!
		if(result > 0) {
			// > 조회수 증가에 성공한 경우
			
			Board b = boardService.selectBoard(boardNo);
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(b);
			
		} else {
			// > 조회수 증가에 실패한 경우
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(null);
		}
	}
	
	// 첨부파일 다운로드용 컨트롤러
	@GetMapping("/boards/download/{changeName}/{originName}")
	public ResponseEntity<Resource> upfileDownload(@PathVariable String changeName,
												   @PathVariable String originName,
												   HttpSession session) throws IOException {
		
		// 다운로드할 파일의 물리적인 경로 
		String savePath = session.getServletContext().getRealPath("/resources/board_upfiles/");
		
		// System.out.println(savePath);
		// System.out.println(changeName);
		// > savePath + changeName 로 다운로드할 파일의 절대경로를 완성시킬 수 있다!!
		
		// 파일을 그냥 응답데이터로는 못보내소, 응답데이터로 내보낼 수 있게끔 포장해주기
		// > Resource 라는 객체를 이용한다!!
		Resource resource = new FileSystemResource(savePath + changeName);
		
		// 파일이 제대로 존재하는지를 검사 후 응답데이터로 보내기
		if(!resource.exists()) {
			// > 해당 파일이 존재하지 않을 경우
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body(null);
			
		} else {
			// > 해당 파일이 존재할 경우
			
			// 이번에는 응답데이터가 파일로 넘어가야하는 특이케이스이기 떄문에 설정이 이것저것 붙는다!!
			// 우선 한글 파일명 깨짐을 방지
			String encodedName = URLEncoder.encode(originName, "UTF-8");
			// > 사용자가 보기 좋게 원본파일명으로 다운로드를 하기 위함
			
			// ResponseEntity 객체 생성 시 여기에서도 파일 다운로드와 관련된 설정을 추가해야한다!!
			return ResponseEntity.status(HttpStatus.OK)
								 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
								 // 브라우저가 파일을 열지 않고 곧바로 다운로드 처리할 수 있게끔 해주는 설정
								 .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(savePath + changeName)))
								 // 파일의 MIME 타입을 지정하는 부분 (원본 파일의 타입으로 알아서 지정하도록 유도)
								 .body(resource);
			
			/*
			 * * 요청에 Header, Body 가 있듯이, 응답에서도 Header 와 Body 가 있음!!
			 * - 요청에 Header : 설정값들, GET 방식일 경우 전달할 데이터들
			 *   요청의 Boby : POST 방식일 경우 전달할 데이터들
			 * - 응답의 Header : 설정값들
			 *   응답의 Body : 응답데이터
			 */
		}
	}
	
	// 수정용 일반게시글 상세 조회용 컨트롤러
	@GetMapping("/boards/{boardNo}/form")
	public ResponseEntity<Board> selectBoardForm(@PathVariable int boardNo) {
		
		// 컨트롤러는 재활용이 안되지만, 서비스부터는 재활용 가능!!
		Board b = boardService.selectBoard(boardNo);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(b);
	}
	
	// 일반게시글 수정용 컨트롤러
	@PostMapping("/boards/{boardNo}")
	public ResponseEntity<String> updateBoard(@PathVariable int boardNo,
											  Board b,
											  MultipartFile reupfile,
											  HttpSession session) {
		
		// System.out.println(boardNo);
		
		System.out.println(b);
		// > 기존에 첨부파일이 있다면 originName, changeName 필드값이 !equals("null")
		
		// 기존 첨부파일이 없을 경우 "null" --> null
		if((b.getOriginName().equals("null")) && (b.getChangeName().equals("null"))) {
			
			b.setOriginName(null);
			b.setChangeName(null);
		}
		
		// System.out.println(reupfile);
		// > 새로운 첨부파일이 없다면 reupfile == null
		
		// > 새로운 첨부파일이 있을 경우
		if(reupfile != null) {
			// > 새로 넘어온 첨부파일이 있을 경우
			
			// 새로 넘어온 첨부파일에 대해 파일명을 수정 후 서버로 업로드,
			// originName, changeName 필드값을 b 에 새로이 셋팅
			
			String originName = reupfile.getOriginalFilename();
			
			String changeName = FileRenamePolicy.saveFile(reupfile, session,
															"/resources/board_upfiles/");
			
			b.setOriginName(originName);
			b.setChangeName(changeName);
		}
		
		// 이 시점 기준으로
		// > 기존 첨부파일 X 새로운 첨부파일 X - originName, changeName 필드가 모두 null
		//   기존 첨부파일 X 새로운 첨부파일 O - originName, changeName 필드가 "새로운 첨부파일" 기준으로 셋팅
		//	 기존 첨부파일 O 새로운 첨부파일 X - originName, changeName 필드가 "기존 첨부파일" 기준으로 셋팅
		//	 기존 첨부파일 O 새로운 첨부파일 O - originName, changeName 필드가 "새로운 첨부파일" 기준으로 셋팅
		
		// > 이전에는 Board 테이블과 Attachment 테이블을 나눠서 설계하고 구현했었음!!
		//   (게시글 하나 당 첨부파일이 여러개 들어갈 수 있으므로 제1정규화를 진행한거였음 - 이게 정석 방법)
		// > 위의 경우의 수를 잘 파악해서 로직을 짜야함!!
		
		// > 이번에는 Board 테이블에 따로 쪼개지 않고 첨부파일 정보도 같이 저장할 수 있도롣 컬럼으로 구성해놨음!!
		//   (게시글 하나 당 첨부파일이 하나만 들어갈 수 있음, 여러개가 들어가는 순간 원자성이 어긋남)
		// > 위의 경우의 수를 따지지 않고 그냥 무조건 Board 테이블에 update 만 해주면 끝남
		
		// 서비스 호출
		Board updateBo = boardService.updateBoard(b);
		
		String message = (updateBo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 일반게시글 삭제용 컨트롤러
	@DeleteMapping("/boards/{boardNo}")
	public ResponseEntity<String> deleteBoard(@PathVariable int boardNo) {
		
		// 서비스 호출
		int result = boardService.deleteBoard(boardNo);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 댓글 조회용 컨트롤러
	@GetMapping("/replys/{boardNo}")
	public ResponseEntity<List<Reply>> selectReplyList(@PathVariable int boardNo) {
		
		List<Reply> list = boardService.selectReplyList(boardNo);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
	}
	
	// 댓글 작성용 컨트롤러
	@PostMapping("/replys")
	public ResponseEntity<String> insertReply(HttpServletRequest request, Reply r, int boardNo) {
		
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY
										.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		String userId = claims.getSubject();
		
		Member m = memberService.selectMember(userId);
		Board b = boardService.selectBoard(boardNo);
		
		r.setBoard(b);
		r.setMember(m);
		
		Reply insertR = boardService.insertReply(r);
		
		String result = (insertR != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(result);
	}
	
	// 댓글 수정용 컨트롤러
	@PutMapping("/replys")
	public ResponseEntity<String> updateReply(HttpServletRequest request, Reply r, int boardNo) {
		
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY
										.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		String userId = claims.getSubject();
		
		Member m = memberService.selectMember(userId);
		Board b = boardService.selectBoard(boardNo);
		
		r.setBoard(b);
		r.setMember(m);
		
		Reply updateR = boardService.updateReply(r);
		
		String result = (updateR != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(result);
	}
	
	// 댓글 삭제용 컨트롤러
	@DeleteMapping("/replys/{replyNo}")
	public ResponseEntity<String> deleteReply(@PathVariable int replyNo) {
		
		int num = boardService.deleteReply(replyNo);
		
		String result = (num > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(result);
	}
	
}