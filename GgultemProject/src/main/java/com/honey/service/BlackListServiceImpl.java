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
        
        // 수동 매핑 (안전)
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

        // 엔티티의 Builder 패턴을 사용하여 필수 값(startDate, enabled)을 수동 할당
        BlackList blackList = BlackList.builder()
                .email(blackListDTO.getEmail())
                .reason(blackListDTO.getReason())
                .adminId(blackListDTO.getAdminId() != null ? blackListDTO.getAdminId() : "admin_01")
                .status("Y") // 초기 상태 강제 주입
                .startDate(LocalDateTime.now()) // 💡 엔티티 필드에 현재 시간 주입
                .endDate(blackListDTO.getEndDate())
                .enabled(1) // 💡 활성화 상태(1) 주입
                .build();
        
        return repository.save(blackList).getBlId();
    }

    @Override
    public PageResponseDTO<BlackListDTO> list(SearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, 
                searchDTO.getSize(), Sort.by("blId").descending());
        
        Page<BlackList> result = repository.findAllByEnabled(pageable);
        
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

        // 수동 필드 업데이트 (null 방지)
        blackList.setReason(blackListDTO.getReason());
        if(blackListDTO.getAdminId() != null) blackList.setAdminId(blackListDTO.getAdminId());
        if(blackListDTO.getStatus() != null) blackList.setStatus(blackListDTO.getStatus());
        blackList.setEndDate(blackListDTO.getEndDate());

        repository.save(blackList);
    }
    
    @Override
    public void remove(Long blId) {
        log.info("--- 블랙리스트 삭제(비활성화) 시도 --- ID: " + blId);
        
        BlackList blackList = repository.findById(blId)
                .orElseThrow(() -> new RuntimeException("삭제할 대상이 없습니다."));
        
        // 실제로 삭제하지 않고 enabled를 0으로 변경 (Soft Delete)
        blackList.setEnabled(0);
        repository.save(blackList);
    }
}