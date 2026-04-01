package com.honey.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.ItemBoard;

public interface ItemBoardRepository extends JpaRepository<ItemBoard, Long> {

	@Query("select i from ItemBoard i where i.enabled = 1")
	Page<ItemBoard> findAllList(Pageable pageable);

	// [기본 검색] 제목, 작성자 등 단일 조건 검색
	@EntityGraph(attributePaths = { "itemList" })
	@Query("SELECT i FROM ItemBoard i WHERE i.enabled >= 1 AND ( "
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

	// [필터링 검색] 지도 클릭 및 카테고리 등 복합 조건 검색 (수정됨)
	@EntityGraph(attributePaths = { "itemList" })
	@Query("""
			SELECT i FROM ItemBoard i
			WHERE i.enabled >= 1
			AND (:email IS NULL OR :email = '' OR :email = 'all' OR i.member.email = :email)
			AND (
			    :status = 'all' OR
			    LOWER(TRIM(i.status)) = LOWER(TRIM(:status)) OR
			    (i.status LIKE CONCAT('%', :status, '%'))
			)
			AND (:category = 'all' OR i.category = :category)
			AND (
			    :location = 'all' OR
			    i.location = :location OR
			    i.location LIKE CONCAT('%', :location, '%')
			)
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
			@Param("email") String email, Pageable pageable);

	@Query("""
			SELECT i FROM ItemBoard i
			WHERE i.lat BETWEEN :minLat AND :maxLat
			AND i.lng BETWEEN :minLng AND :maxLng
			AND i.enabled >= 1
			""")
	List<ItemBoard> findByBoundary(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
			@Param("minLng") Double minLng, @Param("maxLng") Double maxLng);
	
	
}