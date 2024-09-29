package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Place;

public interface PlaceSearch {
    Page<Place> searchAll(Pageable pageable);
}
