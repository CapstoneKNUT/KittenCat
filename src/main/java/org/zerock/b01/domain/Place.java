package org.zerock.b01.domain;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @Id
    private Integer pord;

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
