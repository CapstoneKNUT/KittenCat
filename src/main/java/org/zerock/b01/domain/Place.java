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
@ToString(exclude = "imageSet")
public class Place extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }


    @OneToMany(mappedBy = "place",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<PlaceImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){

        PlaceImage placeImage = PlaceImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .place(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(placeImage);
    }

    public void clearImages() {

        imageSet.forEach(placeImage -> placeImage.changePlace(null));

        this.imageSet.clear();
    }

}
