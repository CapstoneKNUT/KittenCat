package org.zerock.b01.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planNo;

    private Boolean isCar;

    @Column(length = 50, nullable = false)
    private String writer;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @OneToMany(mappedBy = "planSet", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<PlanPlace> planPlaceSet = new HashSet<>();

    // 추가할 생성자
    public PlanSet(Long planNo) {
        this.planNo = planNo;
    }

}
