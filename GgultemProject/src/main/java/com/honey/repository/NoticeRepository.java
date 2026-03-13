package com.honey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>{

	//List<Notice> findByStopEndDateBeforeAndEnabledIn(LocalDateTime now, List<Integer> statuses);

}
