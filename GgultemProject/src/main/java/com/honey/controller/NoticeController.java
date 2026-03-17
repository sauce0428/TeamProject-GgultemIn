//package com.honey.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.honey.dto.NoticeDTO;
//import com.honey.dto.PageRequestDTO;
//import com.honey.dto.PageResponseDTO;
//import com.honey.dto.SearchDTO;
//import com.honey.service.NoticeService;
//import com.honey.util.CustomFileUtil;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//
//@RestController
//@RequiredArgsConstructor
//@Log4j2
//@RequestMapping("/admin/notice")
//public class NoticeController {
//	
//	private final NoticeService service;
//	private final CustomFileUtil fileUtil;
//
//	@GetMapping("/{noticeId}")
//	public NoticeDTO getNotice(@PathVariable(name = "noticeId") Long noticeId) {
//		return service.get(noticeId);
//	}
//	
//	@PostMapping("/")
//	public Map<String, Long> register(NoticeDTO noticeDTO){
//		
//		List<MultipartFile> files = noticeDTO.getFiles();
//        List<String> uploadFileNames = fileUtil.saveFiles(files);
//        
//		noticeDTO.setUploadFileNames(uploadFileNames);
//		
//		Long noticeId = service.register(noticeDTO);
//		return Map.of("NO", noticeId);
//	}
//	
//	@GetMapping("/list")
//	public PageResponseDTO<NoticeDTO> list(SearchDTO searchDTO){
//		log.info(searchDTO);
//		return service.list(searchDTO);
//	}
//	
//	@PutMapping("/{noticeId}")
//	public Map<String, String> modify(@PathVariable(name="noticeId") Long noticeId, NoticeDTO noticeDTO){
//		List<MultipartFile> files = noticeDTO.getFiles();
//        List<String> uploadFileNames = fileUtil.saveFiles(files);
//        
//		noticeDTO.setUploadFileNames(uploadFileNames);
//		noticeDTO.setNoticeId(noticeId);
//		
//		service.modify(noticeDTO);
//		return Map.of("RESULT", "SUCCESS");
//	}
//	
//	@PutMapping("/remove/{noticeId}")
//	public Map<String, String> remove(@PathVariable(name = "noticeId") Long noticeId){
//		service.remove(noticeId);
//		return Map.of("RESULT", "SUCESS");
//	}
//	
//}
//
