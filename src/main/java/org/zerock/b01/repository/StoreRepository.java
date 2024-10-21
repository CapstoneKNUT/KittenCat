package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    
    //list
    Page<Store> findByMid_Mid(String mid, Pageable pageable);

    //read
    Optional<Store> findByMid_MidAndSno(String username, Long sno);

    //delete
    void deleteByMid_MidAndSno(String username, Long sno);

    //searchByName
    @Query("SELECT s FROM Store s WHERE s.mid.mid = :username AND s.p_name LIKE %:name%")
    Page<Store> findByUsernameAndPlaceNameContaining(@Param("username") String username, @Param("name") String p_name, Pageable pageable);
}

