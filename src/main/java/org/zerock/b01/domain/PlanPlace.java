package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ppOrd;

    private String pp_startAddress;

    private LocalDateTime pp_startDate;

    private LocalTime pp_takeDate;

    private int pp_mapx;

    private int pp_mapy;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlanSet planNo;

}
