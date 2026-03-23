package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "username", "password"})
public class MemberDTO extends User {
	
	private static final long serialVersionUID = 1L; 
	private String email;
	private String pw;
	private String nickname;
	private String location;
	private boolean social;
	private String phone;
	private String businessNumber; // 사업자 번호
    private String companyName;    // 상호명
    private int bizMoney;
    private boolean businessVerified;
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
	
	// 1. 기본 생성자 (바인딩을 위해 필요)
    // 부모 클래스인 User에 기본 생성자가 없으므로 super()에 가짜 값을 넣어서라도 생성해야 합니다.
    public MemberDTO() {
        super("dummy", "dummy", new ArrayList<>());
    }
	
	
	public MemberDTO(String email, String pw, String nickname, boolean social, Set<String> roleNames, LocalDateTime regDate) { 
		// 이메일(사용자 아이디), 비밀번호, 권한목록(roles 또는 authorities)을 받아 UserDetails 객체로 만들어준다. 
		super(email, pw, (roleNames == null ? new HashSet<String>() : roleNames).stream().map(str -> 
		new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList())); 
		this.email = email; 
		this.pw = pw; 
		this.nickname = nickname; 
		this.social = social; 
		// 필드도 null 방지
	    this.roleNames = (roleNames == null ? new HashSet<>() : roleNames); 
		this.regDate = regDate;
		} 
	
		public Map<String, Object> getClaims() { 
		Map<String, Object> dataMap = new HashMap<>(); 
		dataMap.put("email", email); 
		dataMap.put("pw", pw); 
		dataMap.put("nickname", nickname); 
		dataMap.put("social", social); 
		dataMap.put("roleNames", roleNames); 
		dataMap.put("regDate", regDate); 
		dataMap.put("bizMoney", bizMoney);
        dataMap.put("businessNumber", businessNumber);
        dataMap.put("enabled", enabled);
        dataMap.put("uploadFileNames", uploadFileNames);
        dataMap.put("phone", phone);
		return dataMap; 
		} 
	
}
