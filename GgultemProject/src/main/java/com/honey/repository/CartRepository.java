package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@Query("select c from Cart c") // 전체 조회를 원하신다면 이렇게!
	Page<Cart> findAllList(Pageable pageable);

	@EntityGraph(attributePaths = { "itemBoard", "itemBoard.itemList" })
	@Query("SELECT c FROM Cart c JOIN c.itemBoard i " + "WHERE c.member.no = :no " + 
			"AND ( " + // 2. 검색 조건 시작
			"  (:searchType = 'title' AND i.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND i.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND i.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'category' AND i.category LIKE %:keyword%) OR "
			+ "  (:searchType = 'location' AND i.location LIKE %:keyword%) OR "
			+ "  ((:searchType = 'all' OR :searchType IS NULL OR :searchType = '') AND " + 
			"   (i.title LIKE %:keyword% OR i.writer LIKE %:keyword% OR i.content LIKE %:keyword% OR i.category LIKE %:keyword% OR i.location LIKE %:keyword%)) "
			+ ")") // 3. 검색 조건 끝
	Page<Cart> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,Pageable pageable,
			@Param("no") Long memberNo);
}
