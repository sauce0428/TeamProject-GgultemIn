package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.Member;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final CustomFileUtil fileUtil;

	@Override
	public MemberDTO get(Long no) {
		java.util.Optional<Member> result = memberRepository.findById(no);
		Member member = result.orElseThrow();

		MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

		List<String> fileNameList = member.getThumbnailList().stream().map(thumbnail -> thumbnail.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			memberDTO.setUploadFileNames(fileNameList);
		} else {
			memberDTO.setUploadFileNames(List.of("default.jpg"));
		}

		return memberDTO;
	}

	@Override
	public Long register(MemberDTO memberDTO) {
		Member member = modelMapper.map(memberDTO, Member.class);

		member.changeStatus(1);
		member.addRole("ROLE_MEMBER");

		Member savedMember = memberRepository.save(member);

		return savedMember.getNo();
	}

	@Override
	public void modify(MemberDTO memberDTO) {
		Optional<Member> result = memberRepository.findById(memberDTO.getNo());
		Member member = result.orElseThrow();

		member.changePw(memberDTO.getPw());
		member.changePhone(memberDTO.getPhone());
		member.changeEmail(memberDTO.getEmail());
		member.changeNickName(memberDTO.getNickName());
		
	    if (memberDTO.getEnabled() != null) {
	        if (!member.getEnabled().equals(memberDTO.getEnabled())) {
	            member.changeStatus(memberDTO.getEnabled());
	        }
	    }

		memberRepository.save(member);
	}

	@Override
	public void remove(Long no) {
		Optional<Member> result = memberRepository.findById(no);
		Member member = result.orElseThrow();

		member.changeStatus(0);
		
		List<String> oldFileNames = member.getThumbnailList().stream()
	            .map(thumbnail -> thumbnail.getFileName())
	            .collect(Collectors.toList());
	    
	    if (oldFileNames != null && !oldFileNames.isEmpty()) {
	        fileUtil.deleteFiles(oldFileNames);
	    }

	    member.clearList();

		memberRepository.save(member);
	}

	@Override
	public PageResponseDTO<MemberDTO> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				pageRequestDTO.getSize(), Sort.by("no").descending());
		Page<Member> result = memberRepository.findAll(pageable);
		
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> {
	        MemberDTO dto = modelMapper.map(member, MemberDTO.class);

	        List<String> fileNameList = member.getThumbnailList().stream()
	                .map(thumbnail -> thumbnail.getFileName())
	                .collect(Collectors.toList());

	        if (fileNameList != null && !fileNameList.isEmpty()) {
	            dto.setUploadFileNames(fileNameList);
	        } else {
	            dto.setUploadFileNames(List.of("default.jpg"));
	        }

	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<MemberDTO> responseDTO = PageResponseDTO.<MemberDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

	return responseDTO;
	}

	@Override
	public void updateToThumbnail(MemberDTO memberDTO) {
	    Member member = memberRepository.findById(memberDTO.getNo()).orElseThrow();

	    member.clearList();
	    
	    log.info("저장된 이름 = "+memberDTO.getUploadFileNames().toString());
	    
	    List<String> newFileNames = memberDTO.getUploadFileNames();
	    if (newFileNames != null && !newFileNames.isEmpty()) {
	        newFileNames.forEach(fileName -> {
	            member.addImageString(fileName);
	        });
	    }

	    memberRepository.save(member);
	}

}
