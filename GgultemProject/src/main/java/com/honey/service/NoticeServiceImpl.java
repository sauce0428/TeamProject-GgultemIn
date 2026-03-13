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

import com.honey.domain.Member;
import com.honey.domain.Notice;
import com.honey.dto.NoticeDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.NoticeRepository;
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

    @Override
    public NoticeDTO get(Long noticeId) {
        // 1. 조회 (Optional 활용)
        java.util.Optional<Notice> result = noticeRepository.findById(noticeId);
        Notice notice = result.orElseThrow();
        
        notice.changeViewCount(notice.getViewCount() + 1);
        
        // 2. ModelMapper를 이용해 엔티티의 기본 필드를 DTO로 복사
        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);
        
        // 3. Notice 내부에 저장된 이미지 객체(NoticeImage) 리스트에서 파일명(String)만 추출
        List<String> fileNameList = notice.getNoticeImage().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());

        // 4. 추출한 파일명 리스트를 DTO에 세팅 (이미지가 없으면 기본 이미지 제공)
        if (fileNameList != null && !fileNameList.isEmpty()) {
            noticeDTO.setUploadFileNames(fileNameList);
        } else {
            // 공지사항에 이미지가 없을 경우 기본 이미지(예: no-image.jpg) 설정
            noticeDTO.setUploadFileNames(List.of("default.jpg"));
        }
        
        return noticeDTO;
    }

    @Override
    public Long register(NoticeDTO noticeDTO) {
        Member member = Member.builder().no(noticeDTO.getMemberNo()).build();
        
        Notice notice = Notice.builder().title(noticeDTO.getTitle()).content(noticeDTO.getContent()).viewCount(0).member(member).build();
        
        // 이미지 추가 로직 (DTO의 파일명을 Entity로)
        if(noticeDTO.getUploadFileNames() != null) {
            noticeDTO.getUploadFileNames().forEach(fileName -> {
                notice.addImageString(fileName);
            });
        }
        
        // 기본 상태 활성화
        notice.changeStatus(1);
        
        return noticeRepository.save(notice).getNoticeId();
    }

    @Override
    public void modify(NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeDTO.getNoticeId()).orElseThrow();

        // 제목, 내용 수정
        notice.changeTitle(noticeDTO.getTitle());
        notice.changeContent(noticeDTO.getContent());

        // 이미지 교체 로직 (Member의 updateToThumbnail 참고)
        // 기존 파일 삭제
        List<String> oldFileNames = notice.getNoticeImage().stream()
                .map(img -> img.getFileName()).collect(Collectors.toList());
        
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
	        fileUtil.deleteFiles(oldFileNames);
	    }
        
     // [중요] 기존 리스트를 비우고
        notice.clearList(); 

        // [중요] 새 파일명을 추가할 때 noticeImage 리스트 자체가 새로 할당되거나 
        // 제대로 인지되도록 addImageString을 호출
        List<String> newFileNames = noticeDTO.getUploadFileNames();
        if (newFileNames != null && !newFileNames.isEmpty()) {
            newFileNames.forEach(notice::addImageString);
        }

        // 4. 명시적으로 save 호출 (Dirty Checking에만 의존하지 않음)
        noticeRepository.save(notice);
    }

    @Override
    public void remove(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow();
        notice.changeStatus(0); // 논리 삭제 (Member와 동일)
        
     // 2. 기존 이미지 파일들 삭제 (CustomFileUtil 활용)
        List<String> oldFileNames = notice.getNoticeImage().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
        
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            fileUtil.deleteFiles(oldFileNames);
        }

        // 3. 기존 리스트 비우고 새 이미지 파일명 추가
        notice.clearList();
        
        
        noticeRepository.save(notice);
    }

    @Override
    public PageResponseDTO<NoticeDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1, 
                pageRequestDTO.getSize(), 
                Sort.by("noticeId").descending()
        );

        Page<Notice> result = noticeRepository.findAll(pageable);

        List<NoticeDTO> dtoList = result.getContent().stream()
                .map(notice -> modelMapper.map(notice, NoticeDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<NoticeDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }
    
    @Override
    public void updateToImage(NoticeDTO noticeDTO) {
        // 1. 기존 공지사항 찾기
        java.util.Optional<Notice> result = noticeRepository.findById(noticeDTO.getNoticeId());
        Notice notice = result.orElseThrow();

        // 2. 기존 이미지 파일들 삭제 (CustomFileUtil 활용)
        List<String> oldFileNames = notice.getNoticeImage().stream()
                .map(image -> image.getFileName())
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