package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Place;
import org.zerock.b01.domain.QPlace;
import org.zerock.b01.domain.QReply;
import org.zerock.b01.dto.PlaceImageDTO;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.dto.PlaceListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceSearchImpl extends QuerydslRepositorySupport implements PlaceSearch {

    public PlaceSearchImpl(){
        super(Place.class);
    }

    @Override
    public Page<Place> search1(Pageable pageable) {

        QPlace place = QPlace.place;

        JPQLQuery<Place> query = from(place);

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(place.title.contains("11")); // title like ...

        booleanBuilder.or(place.content.contains("11")); // content like ....

        query.where(booleanBuilder);
        query.where(place.bno.gt(0L));


        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Place> list = query.fetch();

        long count = query.fetchCount();


        return null;

    }

    @Override
    public Page<Place> searchAll(String[] types, String keyword, Pageable pageable) {

        QPlace place = QPlace.place;
        JPQLQuery<Place> query = from(place);

        if( (types != null && types.length > 0) && keyword != null ){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(place.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(place.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(place.writer.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }//end if

        //bno > 0
        query.where(place.bno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Place> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }

    @Override
    public Page<PlaceListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QPlace place = QPlace.place;
        QReply reply = QReply.reply;

        JPQLQuery<Place> query = from(place);
        query.leftJoin(reply).on(reply.place.eq(place));

        query.groupBy(place);

        if( (types != null && types.length > 0) && keyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(place.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(place.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(place.writer.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }

        //bno > 0
        query.where(place.bno.gt(0L));

        JPQLQuery<PlaceListReplyCountDTO> dtoQuery = query.select(Projections.bean(PlaceListReplyCountDTO.class,
                place.bno,
                place.title,
                place.writer,
                place.regDate,
                reply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable,dtoQuery);

        List<PlaceListReplyCountDTO> dtoList = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }

    @Override
    public Page<PlaceListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QPlace place = QPlace.place;
        QReply reply = QReply.reply;

        JPQLQuery<Place> placeJPQLQuery = from(place);
        placeJPQLQuery.leftJoin(reply).on(reply.place.eq(place)); //left join

        if( (types != null && types.length > 0) && keyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(place.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(place.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(place.writer.contains(keyword));
                        break;
                }
            }//end for
            placeJPQLQuery.where(booleanBuilder);
        }

        placeJPQLQuery.groupBy(place);

        getQuerydsl().applyPagination(pageable, placeJPQLQuery); //paging



        JPQLQuery<Tuple> tupleJPQLQuery = placeJPQLQuery.select(place, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<PlaceListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Place place1 = (Place) tuple.get(place);
            long replyCount = tuple.get(1,Long.class);

            PlaceListAllDTO dto = PlaceListAllDTO.builder()
                    .bno(place1.getBno())
                    .title(place1.getTitle())
                    .writer(place1.getWriter())
                    .regDate(place1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //PlaceImage를 PlaceImageDTO 처리할 부분
            List<PlaceImageDTO> imageDTOS = place1.getImageSet().stream().sorted()
                    .map(placeImage -> PlaceImageDTO.builder()
                            .uuid(placeImage.getUuid())
                            .fileName(placeImage.getFileName())
                            .ord(placeImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setPlaceImages(imageDTOS);

            return dto;
        }).collect(Collectors.toList());

        long totalCount = placeJPQLQuery.fetchCount();


        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}

















