package org.zerock.b01.domain;

import lombok.*;
import javax.persistence.*;


@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno"),
        @Index(name = "idx_reply_place_bno", columnList = "place_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"board","place"})
//@ToString
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_bno")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_bno")
    private Place place;

    private String replyText;

    private String replyer;

    public void changeText(String text){
        this.replyText = text;
    }

}


