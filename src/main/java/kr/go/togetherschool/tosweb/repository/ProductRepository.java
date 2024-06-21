package kr.go.togetherschool.tosweb.repository;

import kr.go.togetherschool.tosweb.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom query methods here if needed
}

