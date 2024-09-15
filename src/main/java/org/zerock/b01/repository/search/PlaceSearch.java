package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.dto.PlaceListReplyCountDTO;

public interface PlaceSearch {

    Page<Place> search1(Pageable pageable);

    Page<Place> searchAll(String[] types, String keyword, Pageable pageable);

    Page<PlaceListReplyCountDTO> searchWithReplyCount(String[] types,
                                                      String keyword,
                                                      Pageable pageable);

    Page<PlaceListAllDTO> searchWithAll(String[] types,
                                        String keyword,
                                        Pageable pageable);
}
