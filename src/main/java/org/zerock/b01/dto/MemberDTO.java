package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private String mid;

    private String m_pw;

    private String m_email;

    private String m_name;

    private String m_phone;

    private String m_address;

    private String m_birth;

    private String m_gender;

    private String m_mbti;

    private boolean m_del;

    private boolean m_social;

}
