package com.honey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honey.domain.Board;
import com.honey.domain.BoardReply;
import com.honey.domain.Member;
import com.honey.dto.BoardReplyDTO;
import com.honey.repository.BoardReplyRepository;
import com.honey.repository.BoardRepository;
import com.honey.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardReplyServiceImpl implements BoardReplyService {

	private final BoardReplyRepository boardReplyRepository;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	// 댓글 등록
	@Override
	public Long register(BoardReplyDTO dto) {

		Board board = boardRepository.findById(dto.getBoardNo()).orElseThrow();

		Member member = memberRepository.findById(dto.getEmail()).orElseThrow();

		// 부모 댓글 (null값 찾는거방지)
		BoardReply parentReply = null;

		if (dto.getParentReplyNo() != null) {
			parentReply = boardReplyRepository.findById(dto.getParentReplyNo()).orElseThrow();
		}

		BoardReply reply = BoardReply.builder().board(board).member(member).content(dto.getContent())
				.parent(parentReply).enabled(1).build();

		boardReplyRepository.save(reply);

		return reply.getReplyNo();
	}

	// 댓글 목록 (🔥 트리 구조 완성)
	@Override
	@Transactional(readOnly = true)
	public List<BoardReplyDTO> list(Integer boardNo) {

		List<BoardReply> replyList = boardReplyRepository.findByBoardBoardNoAndEnabled(boardNo, 1);

		// DTO 변환 + Map 생성
		Map<Long, BoardReplyDTO> dtoMap = replyList.stream()
				.map(reply -> BoardReplyDTO.builder().replyNo(reply.getReplyNo()).boardNo(reply.getBoard().getBoardNo())
						.email(reply.getMember().getEmail()).content(reply.getContent())
						.parentReplyNo(reply.getParent() != null ? reply.getParent().getReplyNo() : null)
						.enabled(reply.getEnabled()).regDate(reply.getRegDate()).updDate(reply.getUpdDate()).build())
				.collect(Collectors.toMap(BoardReplyDTO::getReplyNo, dto -> dto));

		// 트리 구조 만들기
		List<BoardReplyDTO> result = new ArrayList<>();

		for (BoardReplyDTO dto : dtoMap.values()) {

			if (dto.getParentReplyNo() == null) {
				// 부모 댓글
				result.add(dto);
			} else {
				// 자식 댓글 → 부모에 연결
				BoardReplyDTO parent = dtoMap.get(dto.getParentReplyNo());
				if (parent != null) {
					parent.getChildList().add(dto);
				}
			}
		}

		return result;
	}

	// 댓글 수정
	@Override
	public void modify(BoardReplyDTO dto) {

		BoardReply reply = boardReplyRepository.findById(dto.getReplyNo()).orElseThrow();

		reply.changeContent(dto.getContent());
	}

	// 댓글 삭제 (논리삭제)
	@Override
	public void remove(Long replyNo) {

		BoardReply reply = boardReplyRepository.findById(replyNo).orElseThrow();

		reply.changeEnabled(0);
	}
}