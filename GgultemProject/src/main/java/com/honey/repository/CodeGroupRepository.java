package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.CodeGroup;


public interface CodeGroupRepository extends JpaRepository<CodeGroup, Long> {
	
	@Query("select c from CodeGroup c where enabled = 1")
	Page<CodeGroup> findAllByEnabled(Pageable pageable);

}
