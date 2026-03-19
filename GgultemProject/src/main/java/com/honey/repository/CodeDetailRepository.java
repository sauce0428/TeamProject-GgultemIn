package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.CodeDetail;


public interface CodeDetailRepository extends JpaRepository<CodeDetail, String> {
	
	@Query("select c from CodeDetail c where enabled = 1")
	Page<CodeDetail> findAllByEnabled(Pageable pageable);

}
