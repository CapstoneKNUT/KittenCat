package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String p_area;

    private String p_subArea;

    private String p_category;

    private String p_keyword;

    public String getP_area(){
        if(p_area == null || p_area.isEmpty()){
            return null;
        }
        return p_area;
    }

    public String getP_subArea(){
        if(p_subArea == null || p_subArea.isEmpty()){
            return null;
        }
        return p_subArea;
    }

    public String getP_category(){
        if(p_category == null || p_category.isEmpty()){
            return null;
        }
        return p_category;
    }

    public String getP_keyword(){
        if(p_keyword == null || p_keyword.isEmpty()){
            return null;
        }
        return p_keyword;
    }

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page -1, this.size, Sort.by(props).ascending());
    }

    private String link;

    public String getLink() {

        if(link == null){
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);

            builder.append("&size=" + this.size);

            if(p_area!=null && p_area.length()>0){
                builder.append("&p_area=" + p_area);
            }

            if(p_subArea!=null && p_subArea.length()>0){
                builder.append("&p_subArea=" + p_subArea);
            }

            if(p_category!=null && p_category.length()>0){
                builder.append("&p_category=" + p_category);
            }

            if(p_keyword != null){
                try {
                    builder.append("&p_keyword=" + URLEncoder.encode(p_keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
            link = builder.toString();
        }

        return link;
    }
}
