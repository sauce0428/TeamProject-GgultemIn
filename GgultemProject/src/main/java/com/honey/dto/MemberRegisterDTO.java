package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterDTO {
	
	private String email;
	private String pw;
	private String nickname;
	private String location;
	private boolean social;
	private String phone;
	private String businessNumber; // 사업자 번호
    private String companyName;    // 상호명
    private int bizMoney;
	private Integer enabled;
	private Set<String> roleNames = new HashSet<>();
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime stopDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime stopEndDate;
	
	@Builder.Default
	@com.fasterxml.jackson.annotation.JsonIgnore
	private List<MultipartFile> files = new ArrayList<>();
	
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();
}
