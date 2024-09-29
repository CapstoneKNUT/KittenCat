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

import java.util.List;
import java.util.stream.Collectors;

public class PlaceSearchImpl extends QuerydslRepositorySupport implements PlaceSearch {

    public PlaceSearchImpl(){
        super(Place.class);
    }

    @Override
    public Page<Place> searchAll(Pageable pageable) {

        QPlace place = QPlace.place;
        JPQLQuery<Place> query = from(place);

        //bno > 0
        query.where(place.bno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Place> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);
    }
}

















