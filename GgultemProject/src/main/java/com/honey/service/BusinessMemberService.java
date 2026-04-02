package com.honey.service;

import java.util.List;
import java.util.Map;

import com.honey.dto.BizMoneyHistoryDTO;
import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.MemberBizMoneySummary;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface BusinessMemberService {

	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO);

	public void memberBusinessRegister(MemberDTO MemberDTO);

	public void approve(String email);
	
	public void reject(String email);

	public void modify(BusinessMemberDTO bMemberDTO);

	public boolean verifyBusinessNumber(String cleanBNo);

	public MemberDTO get(String email);

	public void chargeMoney(String email, Long amount);

	public PageResponseDTO<BizMoneyHistoryDTO> getBizMoneyHistory(SearchDTO searchDTO, String email);

	public void spendMoneyByClick(String email, Long amount, String title);

	public Long getTodaySpend(String email);

	public Integer getTotalViewCount(String email);

	Long getTotalSpend(String email);

	Integer getTodayViewCount(String email);

	public PageResponseDTO<BizMoneyHistoryDTO> getBizMoneyHistoryAdmin(SearchDTO searchDTO);
	
	public PageResponseDTO<Map<String, Object>> getBizMoneySummary(SearchDTO searchDTO);

	public void confirmPayment(String paymentKey, String orderId, String email,Long amount);

}
