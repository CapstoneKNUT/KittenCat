package org.zerock.b01.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class DateResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    //시작 페이지 번호
    private int start;
    //끝 페이지 번호
    private int end;

    //이전 페이지의 존재 여부
    private boolean prev;
    //다음 페이지의 존재 여부
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public DateResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList){

        this.page = pageRequestDTO.getPage();

        this.dtoList = dtoList;

        this.end =   (int)(Math.ceil(this.page / 10.0 )) *  10;

        this.start = this.end - 9;

        int last =  (int)(Math.ceil((total)));

        this.end =  end > last ? last: end;

        this.prev = this.start > 1;

        this.next =  total > this.end;
    }
}
