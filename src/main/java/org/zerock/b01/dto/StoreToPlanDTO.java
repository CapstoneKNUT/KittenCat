package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreToPlanDTO {
    private Long spNo;

    private String p_name;

    private String p_category;

    private String p_address;

    private String p_content;

    private String p_image;

    private String p_call;

    private Float p_star;

    private String p_site;

    private String p_opentime;

    private String p_park;

}
