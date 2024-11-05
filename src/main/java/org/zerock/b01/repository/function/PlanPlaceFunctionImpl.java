package org.zerock.b01.repository.function;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.*;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PlanPlaceFunctionImpl extends QuerydslRepositorySupport implements PlanPlaceFunction {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public PlanPlaceFunctionImpl(JPAQueryFactory queryFactory) {
        super(PlanPlace.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PlanPlace> findAllByDate(Long planNo, LocalDateTime startOfday, LocalDateTime endOfday) {
        QPlanPlace planPlace = QPlanPlace.planPlace;
        JPQLQuery<PlanPlace> query = from(planPlace);

        return query.where(planPlace.planSet.planNo.eq(planNo)
                        .and(planPlace.pp_startDate.between(startOfday, endOfday)))
                .fetch();
    }

    @Override
    @Transactional
    public void updatePpOrdKey(Long ppOrd){
        QPlanPlace planPlace = QPlanPlace.planPlace;
        QTransportParent transportParent = QTransportParent.transportParent;
        QTransportChild transportChild = QTransportChild.transportParent;

        TransportParent foundTransportParent = queryFactory
                .selectFrom(transportParent)
                .where(transportParent.planPlace.ppOrd.eq(ppOrd))
                .fetchOne();

        Long foundTransportChild = foundTransportParent.getTno();

        List<PlanPlace> updatePlanPlaceList = queryFactory
                .selectFrom(planPlace)
                .where(planPlace.ppOrd.goe(ppOrd))
                .fetch();

        for (PlanPlace updatePlanPlace : updatePlanPlaceList) {
            queryFactory
                    .update(transportParent)
                    .set(transportParent.planPlace, updatePlanPlace)
                    .where(transportParent.planPlace.ppOrd.eq(updatePlanPlace.getPpOrd()-1))
                    .execute();
        }

        List<TransportParent> updateTransportParent = queryFactory
                .selectFrom(transportParent)
                .where(transportParent.tno.goe(foundTransportChild))
                .fetch();

        for(TransportParent updateTransportParent : )
    }

    @Override
    @Transactional
    public void updateTnoKey(Long tno){
        QTransportParent transportParent = QTransportParent.transportParent;
        QTransportChild transportChild = QTransportChild.transportChild;

        List<TransportParent> updateTransportParentList = queryFactory
                .selectFrom(transportParent)
                .where(transportParent.tno.goe(tno))
                .fetch();

        for (TransportParent updateTransportParent : updateTransportParentList) {
            queryFactory
                    .update(transportChild)
                    .set(transportChild.transportParent, updateTransportParent)
                    .where(transportParent.tno.goe(tno))
                    .execute();
        }
    }
}









