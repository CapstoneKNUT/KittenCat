package org.zerock.b01.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingResponse {
    private Route route;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        private Traoptimal[] traoptimal;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Traoptimal {
            private Summary summary;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Summary {
                private Integer duration;
            }
        }
    }
}
