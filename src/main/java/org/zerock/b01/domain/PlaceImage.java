package org.zerock.b01.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "place")
public class PlaceImage implements Comparable<PlaceImage> {

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne
    private Place place;


    @Override
    public int compareTo(PlaceImage other) {
        return this.ord - other.ord;
    }

    public void changePlace(Place place){
        this.place = place;
    }

}
