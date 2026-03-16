package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{

	@Query("select c from Cart c") // 전체 조회를 원하신다면 이렇게!
	Page<Cart> findAllList(Pageable pageable);
}
