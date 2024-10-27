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
@Table(name = "transportchild")
public class TransportChild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tno")
    private TransportParent transportParent;

    private String c_method;

    private LocalTime c_takeTime;

}
