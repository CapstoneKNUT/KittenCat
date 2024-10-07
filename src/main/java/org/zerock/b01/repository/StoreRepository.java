package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Store;
import org.zerock.b01.repository.search.StoreSearch;

public interface StoreRepository extends JpaRepository<Store, String>, StoreSearch {

}


//나중에 수정하기