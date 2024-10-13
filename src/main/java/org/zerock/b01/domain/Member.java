package org.zerock.b01.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
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
