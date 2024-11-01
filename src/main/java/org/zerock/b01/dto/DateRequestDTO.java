package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRequestDTO {

    private LocalDateTime specificDate;

    private LocalDateTime startOfday;

    private LocalDateTime endOfday;

    public void setStartAndEndOfDay(LocalDateTime specificDate) {
        this.startOfday = specificDate.toLocalDate().atStartOfDay();
        this.endOfday = specificDate.toLocalDate().atTime(LocalTime.MAX);
    }

}
