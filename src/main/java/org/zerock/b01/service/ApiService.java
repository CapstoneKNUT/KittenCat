package org.zerock.b01.service;

import org.zerock.b01.dto.PlaceSearchDTO;

public interface ApiService {
    String callExternalApi(String url, PlaceSearchDTO placeSearchDTO);
}
