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

	@Query("select i from ItemBoard i where enabled = 1")
	Page<ItemBoard> findAllList(Pageable pageable);

	// 검색 조건
	@EntityGraph(attributePaths = { "itemList" })
	@Query("SELECT i FROM ItemBoard i WHERE i.enabled = 0 AND ( "
			+ "(:searchType = 'title' AND i.title LIKE %:keyword%) OR "
			+ "(:searchType = 'writer' AND i.writer LIKE %:keyword%) OR "
			+ "(:searchType = 'content' AND i.content LIKE %:keyword%) OR "
			+ "(:searchType = 'category' AND i.category LIKE %:keyword%) OR "
			+ "(:searchType = 'status' AND i.status LIKE %:keyword%) OR "
			+ "(:searchType = 'location' AND i.location LIKE %:keyword%) OR "
			+ "((:searchType = 'all' OR :searchType IS NULL OR :searchType = '') AND "
			+ " (i.title LIKE %:keyword% OR i.writer LIKE %:keyword% OR i.content LIKE %:keyword% OR "
			+ "  i.status LIKE %:keyword% OR i.category LIKE %:keyword% OR i.location LIKE %:keyword%))" + ")")
	Page<ItemBoard> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,
			Pageable pageable);

	// 필터링 조건
	@EntityGraph(attributePaths = { "itemList" })
	@Query("""
			SELECT i FROM ItemBoard i
			WHERE i.enabled = 1

			AND (:status = 'all' OR i.status = :status)
			AND (:category = 'all' OR i.category = :category)
			AND (:location = 'all' OR i.location = :location)

			AND (
			    :keyword IS NULL OR :keyword = '' OR
			    (:searchType = 'title' AND i.title LIKE CONCAT('%', :keyword, '%')) OR
			    (:searchType = 'content' AND i.content LIKE CONCAT('%', :keyword, '%')) OR
			    (:searchType = 'all' AND (
			        i.title LIKE CONCAT('%', :keyword, '%') OR
			        i.content LIKE CONCAT('%', :keyword, '%')
			    ))
			)
			""")
	Page<ItemBoard> searchWithFilter(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("status") String status, @Param("category") String category, @Param("location") String location,
			Pageable pageable);
}
