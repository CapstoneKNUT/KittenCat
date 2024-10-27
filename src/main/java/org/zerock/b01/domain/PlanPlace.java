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

    private String pp_title;

    private String pp_startAddress;

    private LocalDateTime pp_startDate;

    private LocalTime pp_takeDate;

    private Float pp_mapx;

    private Float pp_mapy;

    private Boolean NightToNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planNo")
    private PlanSet planSet;

    @OneToOne(mappedBy = "planPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private TransportParent transportParent;
}
