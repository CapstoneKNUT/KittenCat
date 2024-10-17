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

    //코드 수정을 위한 원본보호차원의 주석처리
    @Override
    public Page<Store> searchAll(String username, Pageable pageable) {

        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store);

        //username은 받아질거라서 if문으로 확인할 필요 X
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(store.mid.mid.eq(username));

        query.where(booleanBuilder);

        //sno > 0
        query.where(store.sno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Store> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }

    /*@Override
    public Page<Store> searchAll(String bookmark, String keyword, Pageable pageable) {

        QStore store = QStore.store;
        JPQLQuery<Store> query = from(store);

        // 공통적으로 사용할 BooleanBuilder 생성
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        //bookmark에 값이 없으면 못보잖슴.
        if(bookmark != null && !bookmark.trim().isEmpty()) {
            booleanBuilder.and(store.bookmark.eq(bookmark));  // bookmark 필드에 대한 조건
        }


        if(keyword != null ){ //키워드가 있다면

            booleanBuilder = new BooleanBuilder(); // (
            booleanBuilder.or(store.p_name.contains(keyword));

            query.where(booleanBuilder);
        }//end

        //sno > 0
        query.where(store.sno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Store> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }*/

}









