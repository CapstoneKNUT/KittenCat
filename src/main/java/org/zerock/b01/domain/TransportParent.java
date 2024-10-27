package org.zerock.b01.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transportparent")
public class TransportParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppOrd")
    private PlanPlace planPlace;

    private Boolean isCar;

    private String t_method;

    private LocalDateTime t_startDateTime;

    private LocalTime t_takeTime;

    private LocalDateTime t_goalDateTime;

    private String writer;

    @OneToMany(mappedBy = "transportParent", cascade = {
            CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<TransportChild> transportChildrenSet = new HashSet<>();

    private Boolean NightToNight;
}
