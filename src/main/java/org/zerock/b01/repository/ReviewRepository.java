package org.zerock.b01.repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Review;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByRnoDesc(Pageable pageable);
}
