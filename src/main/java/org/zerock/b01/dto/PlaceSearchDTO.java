package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceSearchDTO {

    private String p_area;

    private String p_subArea;

    private String p_category;

    private Integer p_count;

    private String p_keyword;
}
