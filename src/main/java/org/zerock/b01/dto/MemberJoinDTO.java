package org.zerock.b01.dto;

import lombok.Data;

@Data
public class MemberJoinDTO {

    private String mid;
    private String mpw;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String birth;
    private String gender;
    private String mbti;

    private boolean del;
    private boolean social;

}
