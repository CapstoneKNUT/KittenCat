package org.zerock.b01.service;

import org.zerock.b01.dto.PlaceSearchDTO;
import org.zerock.b01.dto.TransTimeDTO;

public interface ApiService {
    String callExternalApi(String url, PlaceSearchDTO placeSearchDTO);
    String callTransportApi(String url, TransTimeDTO transTimeDTO);
}
