package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceListAllDTO {

    private Integer p_ord;

    private String p_image;

    private String p_name;

    private String p_category;

    private Float p_star;

    private String bookmark;

    public boolean isBookmark(){
        return bookmark != null;
    };

}
