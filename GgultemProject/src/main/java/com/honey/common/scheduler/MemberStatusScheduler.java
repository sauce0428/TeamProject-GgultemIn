package com.honey.common.scheduler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.honey.domain.Member;
import com.honey.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberStatusScheduler {

    private final MemberRepository memberRepository;

    // 매일 새벽 0시에 실행 (cron 표현식)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoReleaseMemberStatus() {
    	log.info("정지된 회원을 찾습니다.");
        LocalDateTime now = LocalDateTime.now();

        // 1. 정지 종료일(stopEndDate)이 지금보다 이전인데, 아직 정지 중(2, 3)인 사람들을 찾음
        List<Member> expiredMembers = memberRepository.findByStopEndDateBeforeAndEnabledIn(
            now, Arrays.asList(2, 3)
        );

        // 2. 해당 인원들의 상태를 1(활성)로 변경
        for (Member member : expiredMembers) {
            member.changeStatus(1);
        }
    }
}
