package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.honey.domain.Member;
import com.honey.domain.Notice;
import com.honey.domain.SearchLog;
import com.honey.dto.NoticeDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.NoticeRepository;
import com.honey.repository.SearchLogRepository;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

	private final ModelMapper modelMapper;
	private final NoticeRepository noticeRepository;
	private final CustomFileUtil fileUtil;
	private final MemberRepository memberRepository;
	private final SearchLogRepository searchLogRepository;

	@Override
	public NoticeDTO get(Long noticeId) {
		// 1. 조회 (Optional 활용)
		java.util.Optional<Notice> result = noticeRepository.findById(noticeId);
		Notice notice = result.orElseThrow();

		notice.changeViewCount(notice.getViewCount() + 1);

		// 2. ModelMapper를 이용해 엔티티의 기본 필드를 DTO로 복사
		NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);
		
		noticeDTO.setMemberEmail(notice.getMember().getEmail());

		// 3. Notice 내부에 저장된 이미지 객체(NoticeImage) 리스트에서 파일명(String)만 추출
		List<String> fileNameList = notice.getNoticeImage().stream().map(image -> image.getFileName())
				.collect(Collectors.toList());

		// 4. 추출한 파일명 리스트를 DTO에 세팅 (이미지가 없으면 기본 이미지 제공)
		if (fileNameList != null && !fileNameList.isEmpty()) {
			noticeDTO.setUploadFileNames(fileNameList);
		} else {
			// 공지사항에 이미지가 없을 경우 기본 이미지(예: no-image.jpg) 설정
			noticeDTO.setUploadFileNames(List.of());
		}
		
		log.info("내가 뽑아온 noticeDTO 값 : " + noticeDTO);

		return noticeDTO;
	}

	@Override
	public Long register(NoticeDTO noticeDTO) {
	    
	    // 1. [파일 처리] 컨트롤러에서 하던 걸 여기서 수행 (파일명 리스트 확보)
	    List<String> uploadFileNames = fileUtil.saveFiles(noticeDTO.getFiles());
	    
	    // 2. [작성자 매핑] 빌더를 이용해 이메일만 담긴 Member 객체 생성
//	    Member member = Member.builder()
//	            .email(noticeDTO.getMemberEmail())
//	            .build();
	    Member member = memberRepository.findById(noticeDTO.getMemberEmail()).orElseThrow();

	    // 3. [엔티티 빌드] 공지사항 객체 생성
	    Notice notice = Notice.builder()
	            .title(noticeDTO.getTitle())
	            .content(noticeDTO.getContent())
	            .viewCount(0)
	            .member(member) // 가짜 객체지만 FK 저장에는 충분!
	            .enabled(1)     // 활성화 상태로 등록
	            .isPinned(noticeDTO.getIsPinned()) // 상단고정기능
	            .build();

	    // 4. [이미지 연결] 저장된 파일명들을 엔티티의 리스트에 추가
	    if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
	        uploadFileNames.forEach(fileName -> {
	            notice.addImageString(fileName);
	        });
	    }

	    // 5. [저장 및 반환]
	    return noticeRepository.save(notice).getNoticeId();
	}

	@Override
	public void modify(NoticeDTO noticeDTO) {
	    // 1. 기존 데이터 가져오기
	    Notice notice = noticeRepository.findById(noticeDTO.getNoticeId()).orElseThrow();

	    // 2. 제목, 내용, 고정 상태 등 기본 정보 수정
	    notice.changeTitle(noticeDTO.getTitle());
	    notice.changeContent(noticeDTO.getContent());
	    notice.changePinned(noticeDTO.getIsPinned());

	    // 3. 이미지 처리 로직 (핵심!)
	    List<MultipartFile> files = noticeDTO.getFiles();

	    // 새 파일이 업로드된 경우에만 기존 이미지를 지우고 새로 등록합니다.
	    if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
	        
	        // (1) 물리적 파일 삭제 (CustomFileUtil 활용)
	        List<String> oldFileNames = notice.getNoticeImage().stream()
	                .map(img -> img.getFileName())
	                .collect(Collectors.toList());
	        if (oldFileNames != null && !oldFileNames.isEmpty()) {
	            fileUtil.deleteFiles(oldFileNames);
	        }

	        // (2) DB 리스트 비우기
	        notice.clearList();

	        // (3) 새 파일 저장 및 이름 추가
	        List<String> newFileNames = fileUtil.saveFiles(files);
	        if (newFileNames != null && !newFileNames.isEmpty()) {
	            newFileNames.forEach(notice::addImageString);
	        }
	    } 
	    // 만약 새 파일이 없다면 아무 것도 하지 않음. (기존 데이터 유지)

	    // 4. 저장
	    noticeRepository.save(notice);
	}

	@Override
	public void remove(Long noticeId) {
		Notice notice = noticeRepository.findById(noticeId).orElseThrow();
		notice.changeStatus(0); // 논리 삭제 (Member와 동일)

		// 2. 기존 이미지 파일들 삭제 (CustomFileUtil 활용)
		List<String> oldFileNames = notice.getNoticeImage().stream().map(image -> image.getFileName())
				.collect(Collectors.toList());

		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			fileUtil.deleteFiles(oldFileNames);
		}

		// 3. 기존 리스트 비우고 새 이미지 파일명 추가
		notice.clearList();

		noticeRepository.save(notice);
	}


	// 

	@Override
	public PageResponseDTO<NoticeDTO> list(SearchDTO searchDTO) { // 파라미터를 SearchDTO로 변경

		// 1. 페이징 및 정렬 설정
		Pageable pageable = PageRequest.of(
				searchDTO.getPage() - 1,
				searchDTO.getSize(),
				Sort.by("isPinned").descending().and(
				Sort.by("noticeId").descending()));

		Page<Notice> result = null;

		// 2. 검색 조건에 따른 분기 처리
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			SearchLog logEntity = SearchLog.builder().keyword(searchDTO.getKeyword())
					.searchType(searchDTO.getSearchType()).build();
			searchLogRepository.save(logEntity);

			// 검색 수행 (Repository에 작성한 searchByCondition 호출)
			result = noticeRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), pageable);
		} else {
			// 일반 목록 조회 (삭제되지 않은 활성 공지사항만)
			result = noticeRepository.findAllByEnabled(pageable);
		}

		// 3. Entity -> DTO 변환 과정
		List<NoticeDTO> dtoList = result.getContent().stream().map(notice -> {
			NoticeDTO dto = modelMapper.map(notice, NoticeDTO.class);

			// 이미지 파일명 리스트 세팅
			List<String> fileNames = notice.getNoticeImage().stream().map(img -> img.getFileName()).toList();

			// 이미지 없을 때의 기본 처리 (기존 코드의 default.jpg 로직 유지 가능)
			if (fileNames.isEmpty()) {
				dto.setUploadFileNames(List.of());
			} else {
				dto.setUploadFileNames(fileNames);
			}

			// 작성자 이름 세팅 (Member 객체에서 추출)
			if (notice.getMember() != null) {
			    // 닉네임 필드가 nickname으로 하기로 결정했으니 getNickname()
			    dto.setWriter(notice.getMember().getNickname()); 
			}

			return dto;
		}).collect(Collectors.toList());

		// 4. 응답 객체 생성
		return PageResponseDTO.<NoticeDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

	@Override
	public void updateToImage(NoticeDTO noticeDTO) {
		// 1. 기존 공지사항 찾기
		java.util.Optional<Notice> result = noticeRepository.findById(noticeDTO.getNoticeId());
		Notice notice = result.orElseThrow();

		// 2. 기존 이미지 파일들 삭제 (CustomFileUtil 활용)
		List<String> oldFileNames = notice.getNoticeImage().stream().map(image -> image.getFileName())
				.collect(Collectors.toList());

		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			fileUtil.deleteFiles(oldFileNames);
		}

		// 3. 기존 리스트 비우고 새 이미지 파일명 추가
		notice.clearList();
		List<String> newFileNames = noticeDTO.getUploadFileNames();

		if (newFileNames != null && !newFileNames.isEmpty()) {
			newFileNames.forEach(fileName -> {
				notice.addImageString(fileName);
			});
		}

		// 4. 저장
		noticeRepository.save(notice);
	}
}