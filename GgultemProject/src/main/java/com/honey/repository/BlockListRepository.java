package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.BlockList;

public interface BlockListRepository extends JpaRepository<BlockList, Long> {
	
	@Query("select c from BlockList c where enabled = 1")
	Page<BlockList> findAllByEnabled(Pageable pageable);

}
