package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Store;

public interface StoreSearch {

    Page<Store> search1(Pageable pageable);

    Page<Store> searchAll(String[] types, String keyword, Pageable pageable);

    /*Page<StoreListAllDTO> searchWithAll(String[] types,
                                        String keyword,
                                        Pageable pageable);*/
}
