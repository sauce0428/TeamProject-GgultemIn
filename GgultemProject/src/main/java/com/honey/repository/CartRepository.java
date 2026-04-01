package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 리스트에서 장바구니 삭제(상품아이디, 사용자이메일)
	void deleteByItemBoardIdAndMemberEmail(Long itemId, String email);

	@Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.itemBoard.id = :itemId AND c.member.email = :email AND c.enabled = 1")
    boolean existsByItemBoardIdAndMemberEmail(@Param("itemId") Long itemId, @Param("email") String email);
	
	@Query("select c from Cart c")
	Page<Cart> findAllList(Pageable pageable, String memberEmail);

	@EntityGraph(attributePaths = { "itemBoard", "itemBoard.itemList" })
	@Query("SELECT c FROM Cart c JOIN c.itemBoard i " + "WHERE c.member.email = :email " + "AND c.enabled = 1 "
			+ "AND (" + "  (:searchType = 'title' AND i.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND i.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND i.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'category' AND i.category LIKE %:keyword%) OR "
			+ "  (:searchType = 'location' AND i.location LIKE %:keyword%) OR "
			+ "  ((:searchType = 'all' OR :searchType IS NULL OR :searchType = '') AND "
			+ "   (i.title LIKE %:keyword% OR i.writer LIKE %:keyword% OR i.content LIKE %:keyword% OR i.category LIKE %:keyword% OR i.location LIKE %:keyword%))"
			+ ")")
	Page<Cart> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,
			Pageable pageable, @Param("email") String email);
}