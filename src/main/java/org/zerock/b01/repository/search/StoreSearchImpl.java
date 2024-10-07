package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.QStore;
import org.zerock.b01.domain.Store;

import java.util.List;

public class StoreSearchImpl extends QuerydslRepositorySupport implements StoreSearch {

    public StoreSearchImpl(){
        super(Store.class);
    }

    @Override
    public Page<Store> search1(Pageable pageable) {

        QStore store = QStore.store;

        JPQLQuery<Store> query = from(store);

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(store.p_name.contains(store.p_name)); // name like ...

        booleanBuilder.or(store.p_category.contains(store.p_category)); // category like ....

        query.where(booleanBuilder);

        //query.where(store.bno.gt(0L));


        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Store> list = query.fetch();

        long count = query.fetchCount();


        return null;

    }

    @Override
    public Page<Store> searchAll(String[] types, String keyword, Pageable pageable) {

        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store);

        if( (types != null && types.length > 0) && keyword != null ){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t": //이름
                        booleanBuilder.or(store.p_name.contains(keyword));
                        break;
                    case "c": //카테고리
                        booleanBuilder.or(store.p_category.contains(keyword));
                        break;
                    case "w": // 주소
                        booleanBuilder.or(store.p_address.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }//end if

        //bno > 0
        //query.where(store.bno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Store> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }

    //댓글 수
    /*@Override
    public Page<StoreListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QStore store = QStore.store;
        QReply reply = QReply.reply;

        JPQLQuery<Store> query = from(store);
        query.leftJoin(reply).on(reply.store.eq(store));

        query.groupBy(store);

        if( (types != null && types.length > 0) && keyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t": //이름
                        booleanBuilder.or(store.p_name.contains(keyword));
                        break;
                    case "c": //카테고리
                        booleanBuilder.or(store.p_category.contains(keyword));
                        break;
                    case "w": //주소
                        booleanBuilder.or(store.p_address.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }

        //bno > 0
        query.where(store.bno.gt(0L));

        //댓글 수
        *//*JPQLQuery<StoreListReplyCountDTO> dtoQuery = query.select(Projections.bean(StoreListReplyCountDTO.class,
                store.bno,
                store.title,
                store.writer,
                store.regDate,
                reply.count().as("replyCount")
        ));*//*

        this.getQuerydsl().applyPagination(pageable,dtoQuery);

        List<StoreListReplyCountDTO> dtoList = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }*/

    /*@Override
    public Page<StoreListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QStore store = QStore.store;
        QReply reply = QReply.reply;

        JPQLQuery<Store> storeJPQLQuery = from(store);
        storeJPQLQuery.leftJoin(reply).on(reply.store.eq(store)); //left join

        if( (types != null && types.length > 0) && keyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t": //이름
                        booleanBuilder.or(store.p_name.contains(keyword));
                        break;
                    case "c": //카테고리
                        booleanBuilder.or(store.p_category.contains(keyword));
                        break;
                    case "w": //주소
                        booleanBuilder.or(store.p_address.contains(keyword));
                        break;
                }
            }//end for
            storeJPQLQuery.where(booleanBuilder);
        }

        storeJPQLQuery.groupBy(store);

        getQuerydsl().applyPagination(pageable, storeJPQLQuery); //paging



        JPQLQuery<Tuple> tupleJPQLQuery = storeJPQLQuery.select(store, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<StoreListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Store store1 = (Store) tuple.get(store);
            long replyCount = tuple.get(1,Long.class);

            StoreListAllDTO dto = StoreListAllDTO.builder()
                    .bno(store1.getBno())
                    .title(store1.getTitle())
                    .writer(store1.getWriter())
                    .regDate(store1.getRegDate())
                    .replyCount(replyCount)
                    .build();



            return dto;
        }).collect(Collectors.toList());

        long totalCount = storeJPQLQuery.fetchCount();


        return new PageImpl<>(dtoList, pageable, totalCount);
    }
*/
}

















