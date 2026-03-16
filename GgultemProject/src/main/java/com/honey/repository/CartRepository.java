package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.Cart;
import com.honey.domain.ItemBoard;

public interface CartRepository extends JpaRepository<Cart, Long>{

	@Query("select c from Cart c where enabled = 0")
	Page<ItemBoard> findAllList(Pageable pageable);
}
