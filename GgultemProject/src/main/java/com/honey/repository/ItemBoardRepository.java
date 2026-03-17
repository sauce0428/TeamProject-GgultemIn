package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;
import com.honey.domain.Cart;
import com.honey.domain.ItemBoard;

public interface ItemBoardRepository extends JpaRepository<ItemBoard, Long> {

	@Query("select i from ItemBoard i where enabled = 0")
	Page<ItemBoard> findAllList(Pageable pageable);

	@EntityGraph(attributePaths = { "itemList"})
	@Query("SELECT i FROM ItemBoard i WHERE "
			+ "( (:searchType = 'title' AND i.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND i.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND i.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'category' AND i.category LIKE %:keyword%) OR " + // 카테고리 추가
			"  (:searchType = 'location' AND i.location LIKE %:keyword%) OR " + // 지역 추가
			"  (:searchType = 'all' AND (i.title LIKE %:keyword% OR i.writer LIKE %:keyword% OR i.content LIKE %:keyword% OR i.category LIKE %:keyword% OR i.location LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (i.title LIKE %:keyword% OR i.writer LIKE %:keyword% OR i.content LIKE %:keyword% OR i.category LIKE %:keyword% OR i.location LIKE %:keyword%) )")
	Page<ItemBoard> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,
			Pageable pageable);
}
