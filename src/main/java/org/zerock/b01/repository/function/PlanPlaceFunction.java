package org.zerock.b01.repository.function;

import org.zerock.b01.domain.PlanPlace;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanPlaceFunction {
    List<PlanPlace> findAllByDate(Long planNo, LocalDateTime startOfDay, LocalDateTime endOfDay);

    void updatePpOrdKey(Long ppOrd);

    void updateTnoKey(Long tno);
}
