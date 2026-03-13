package com.honey.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.MemberDTO;
import com.honey.service.MemberService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/mypage")
public class MypageController {
	
	private final MemberService service;
	private final CustomFileUtil fileUtil;
	
    @GetMapping("/{no}")
    public MemberDTO getMyInfo(@PathVariable(name = "no") Long no) {
        return service.get(no);
    }
    
    @PutMapping("/{no}")
    public Map<String, String> modify(@PathVariable(name = "no") Long no, MemberDTO memberDTO) {
        service.modify(memberDTO);
    	return Map.of("RESULT", "SUCCESS");
    }
    
    @PutMapping("remove/{no}")
    public Map<String, String> remove(@PathVariable(name = "no") Long no) {
        service.remove(no);
        return Map.of("RESULT", "SUCCESS");
    }

    @PutMapping("/thumbnail/{no}")
    public Map<String, String> updateMyThumbnail(@PathVariable(name="no") Long no, MemberDTO memberDTO) {
    	memberDTO.setNo(no);
    	MemberDTO oldMemberDTO = service.get(no);
    	
    	List<String> oldFileNames = oldMemberDTO.getUploadFileNames();
        
        List<MultipartFile> files = memberDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);
        
        List<String> uploadFileNames = memberDTO.getUploadFileNames();
        
        if(currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
        	uploadFileNames.addAll(currentUploadFileNames);
        }

        memberDTO.setUploadFileNames(uploadFileNames);
        
        service.updateToThumbnail(memberDTO); 
        
        if(oldFileNames != null && !oldFileNames.isEmpty()) {
        	List<String> removeFiles = oldFileNames.stream().filter(fileName ->
        			uploadFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
        fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("RESULT", "SUCCESS", "FILE_NAMES", uploadFileNames.toString());
    }
	
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}
}
