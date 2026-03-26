package com.honey.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.BlackList;
import com.honey.dto.BlackListDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BlackListRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {
    
    private final BlackListRepository repository;

    @Override
    public BlackListDTO get(Long blId) {
        BlackList blackList = repository.findById(blId)
                .orElseThrow(() -> new RuntimeException("해당 블랙리스트가 존재하지 않습니다. ID: " + blId));
        
        return BlackListDTO.builder()
                .blId(blackList.getBlId())
                .email(blackList.getEmail())
                .reason(blackList.getReason())
                .adminId(blackList.getAdminId())
                .status(blackList.getStatus())
                .startDate(blackList.getStartDate())
                .endDate(blackList.getEndDate())
                .enabled(blackList.getEnabled())
                .build();
    }
    
    @Override
    public Long register(BlackListDTO blackListDTO) {
        log.info("--- Register Request: " + blackListDTO);

        // 💡 [중복 체크] 이미 'Y' 상태인 데이터가 있는지 확인
        Optional<BlackList> existingBlock = repository.findByEmailAndStatus(blackListDTO.getEmail(), "Y");

        if (existingBlock.isPresent()) {
            throw new RuntimeException("해당 계정은 이미 차단 활성 상태(Y)입니다.");
        }

        BlackList blackList = BlackList.builder()
                .email(blackListDTO.getEmail())
                .reason(blackListDTO.getReason())
                .adminId("관리자")
                .status("Y") 
                .startDate(LocalDateTime.now()) 
                .endDate(blackListDTO.getEndDate())
                .enabled(1) 
                .build();
        
        return repository.save(blackList).getBlId();
    }

    @Override
    public PageResponseDTO<BlackListDTO> list(SearchDTO searchDTO) {
        log.info("--- List with Search: " + searchDTO);

        // 1. 페이징 설정 (PageRequestDTO로부터 상속받은 getPage, getSize 사용)
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, 
                searchDTO.getSize(), Sort.by("blId").descending());
        
        Page<BlackList> result;
        
        // 💡 SearchDTO의 필드명에 맞춰 수정 (searchType, keyword)
        String searchType = searchDTO.getSearchType(); 
        String keyword = searchDTO.getKeyword();

        // 2. 검색 로직 분기 처리
        if (keyword != null && !keyword.trim().isEmpty()) {
            // e: 이메일 검색, r: 사유 검색
            if ("e".equals(searchType)) { 
                result = repository.findByEmailContaining(keyword, pageable);
            } else if ("r".equals(searchType)) { 
                result = repository.findByReasonContaining(keyword, pageable);
            } else { 
                // 검색 타입이 없거나 일치하지 않으면 전체 조회
                result = repository.findAll(pageable);
            }
        } else {
            // 검색어가 없으면 전체 리스트 반환
            result = repository.findAll(pageable);
        }
        
        // 3. 엔티티 리스트를 DTO 리스트로 변환
        List<BlackListDTO> dtoList = result.getContent().stream().map(blackList -> 
            BlackListDTO.builder()
                .blId(blackList.getBlId())
                .email(blackList.getEmail())
                .reason(blackList.getReason())
                .adminId(blackList.getAdminId())
                .status(blackList.getStatus())
                .startDate(blackList.getStartDate())
                .endDate(blackList.getEndDate())
                .enabled(blackList.getEnabled())
                .build()
        ).collect(Collectors.toList());

        // 4. 페이징 결과 반환
        return PageResponseDTO.<BlackListDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }
    
    @Override
    public void modify(BlackListDTO blackListDTO) {
        BlackList blackList = repository.findById(blackListDTO.getBlId())
                .orElseThrow(() -> new RuntimeException("수정 대상을 찾을 수 없습니다."));

        blackList.setReason(blackListDTO.getReason());
        if(blackListDTO.getAdminId() != null) blackList.setAdminId(blackListDTO.getAdminId());
        if(blackListDTO.getStatus() != null) blackList.setStatus(blackListDTO.getStatus());
        blackList.setEndDate(blackListDTO.getEndDate());

        repository.save(blackList);
    }
    
    @Override
    public void remove(Long blId) {
        log.info("--- 블랙리스트 차단 해제 시도 --- ID: " + blId);
        
        BlackList blackList = repository.findById(blId)
                .orElseThrow(() -> new RuntimeException("대상을 찾을 수 없습니다."));
        
        // 상태값만 'N'으로 변경하여 논리적 삭제(차단 해제) 처리
        blackList.setStatus("N"); 
        blackList.setEndDate(LocalDateTime.now());
        
        repository.save(blackList);
    }
}