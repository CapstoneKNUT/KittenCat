package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Place;
import org.zerock.b01.domain.PlaceImage;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.dto.PlaceListReplyCountDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class PlaceRepositoryTests {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Place place = Place.builder()
                    .title("title..." +i)
                    .content("content..." + i)
                    .writer("user"+ (i % 10))
                    .build();

            Place result = placeRepository.save(place);
            log.info("BNO: " + result.getBno());
        });
    }

    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Place> result = placeRepository.findById(bno);

        Place place = result.orElseThrow();

        log.info(place);

    }

    @Test
    public void testUpdate() {

        Long bno = 10L;

        Optional<Place> result = placeRepository.findById(bno);

        Place place = result.orElseThrow();

        place.change("update..title 100", "update content 100");

        placeRepository.save(place);

    }

    @Test
    public void testDelete() {
        Long bno = 80L;

        placeRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {

        //1 page order by bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Place> result = placeRepository.findAll(pageable);


        log.info("total count: "+result.getTotalElements());
        log.info( "total pages:" +result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());

        List<Place> todoList = result.getContent();

        todoList.forEach(place -> log.info(place));


    }

    @Test
    public void testSearch1() {

        //2 page order by bno desc
        Pageable pageable = PageRequest.of(1,10, Sort.by("bno").descending());

        placeRepository.search1(pageable);

    }

    @Test
    public void testSearchAll() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Place> result = placeRepository.searchAll(types, keyword, pageable );

    }

    @Test
    public void testSearchAll2() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Place> result = placeRepository.searchAll(types, keyword, pageable );

        //total pages
        log.info(result.getTotalPages());

        //pag size
        log.info(result.getSize());

        //pageNumber
        log.info(result.getNumber());

        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(place -> log.info(place));
    }


    @Test
    public void testSearchReplyCount() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<PlaceListReplyCountDTO> result = placeRepository.searchWithReplyCount(types, keyword, pageable );

        //total pages
        log.info(result.getTotalPages());
        //pag size
        log.info(result.getSize());
        //pageNumber
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(place -> log.info(place));
    }

    @Test
    public void testInsertWithImages() {

        Place place = Place.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {

            place.addImage(UUID.randomUUID().toString(), "file"+i+".jpg");

        }//end for

        placeRepository.save(place);
    }

//    @Test
//    public void testReadWithImages() {
//
//        //반드시 존재하는 bno로 확인
//        Optional<Place> result = placeRepository.findById(1L);
//
//        Place place = result.orElseThrow();
//
//        log.info(place);
//        log.info("--------------------");
//        log.info(place.getImageSet());
//    }
    @Test
    public void testReadWithImages() {

        //반드시 존재하는 bno로 확인
        Optional<Place> result = placeRepository.findByIdWithImages(13L);

        Place place = result.orElseThrow();

        log.info(place);
        log.info("--------------------");
        for (PlaceImage placeImage : place.getImageSet()) {
            log.info(placeImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages() {

        Optional<Place> result = placeRepository.findByIdWithImages(23L);

        Place place = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        place.clearImages();

        //새로운 첨부파일들
        for (int i = 0; i < 2; i++) {

            place.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
        }

        placeRepository.save(place);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {

        Long bno =61L;
        placeRepository.deleteById(bno);
    }

    @Test
    public void testInsertAll() {

        for (int i = 1; i <= 100; i++) {

            Place place  = Place.builder()
                    .title("Title.."+i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {

                if(i % 5 == 0){
                    continue;
                }
                place.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            placeRepository.save(place);

        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount() {

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        //placeRepository.searchWithAll(null, null,pageable);

        Page<PlaceListAllDTO> result = placeRepository.searchWithAll(null,null,pageable);

        log.info("---------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(placeListAllDTO -> log.info(placeListAllDTO));


    }
}










