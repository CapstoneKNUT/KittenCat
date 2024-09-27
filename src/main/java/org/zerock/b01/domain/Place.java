package org.zerock.b01.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Place extends BaseEntity {

    @Id
    private Integer p_ord;

    @Column(length = 255)
    private String p_name;

    @Column(length = 255)
    private String p_category;

    @Column(length = 500)
    private String p_address;

    @Column(length = 2000)
    private String p_content;

    @Column(length = 255)
    private String bookmark;

    @Column(length = 255)
    private String p_image;

    @Column(length = 255)
    private String p_call;

    @Column(length = 255)
    private Float p_star;

    @Column(length = 255)
    private String p_site;
}
