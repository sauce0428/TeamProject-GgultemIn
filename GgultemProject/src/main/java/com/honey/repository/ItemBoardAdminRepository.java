package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.ItemBoard;

public interface ItemBoardAdminRepository extends JpaRepository<ItemBoard, Long> {

	@Query("select i from ItemBoard i where enabled = 1")
	Page<ItemBoard> findAllList(Pageable pageable);

	@EntityGraph(attributePaths = { "itemList", "member" })
	@Query("SELECT i FROM ItemBoard i LEFT JOIN i.member m " + "WHERE i.enabled = 1 AND ("
			+ "(:searchType = 'title' AND i.title LIKE CONCAT('%', :keyword, '%')) OR "
			+ "(:searchType = 'writer' AND m.nickname LIKE CONCAT('%', :keyword, '%')) OR "
			+ "(:searchType = 'content' AND i.content LIKE CONCAT('%', :keyword, '%')) OR "
			+ "(:searchType = 'all' AND (i.title LIKE CONCAT('%', :keyword, '%') OR m.nickname LIKE CONCAT('%', :keyword, '%'))) OR "
			+ "(:searchType IS NULL OR :searchType = '' OR i.title LIKE CONCAT('%', :keyword, '%') OR m.nickname LIKE CONCAT('%', :keyword, '%'))"
			+ ")")
	Page<ItemBoard> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,
			Pageable pageable);
}