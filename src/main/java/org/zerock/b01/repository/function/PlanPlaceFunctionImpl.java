package org.zerock.b01.repository.function;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.QPlanPlace;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PlanPlaceFunctionImpl extends QuerydslRepositorySupport implements PlanPlaceFunction {

    public PlanPlaceFunctionImpl() {super(PlanPlace.class);}

    @Override
    public List<PlanPlace> findAllByDate(Long planNo, LocalDateTime startOfday, LocalDateTime endOfday) {
        QPlanPlace planPlace = QPlanPlace.planPlace;
        JPQLQuery<PlanPlace> query = from(planPlace);

        return query.where(planPlace.planSet.planNo.eq(planNo)
                        .and(planPlace.pp_startDate.between(startOfday, endOfday)))
                .fetch();
    }

}









