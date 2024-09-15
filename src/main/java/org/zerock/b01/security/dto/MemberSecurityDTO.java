//실제 로그인을 담당하는 파일
package org.zerock.b01.security.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User {

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

    private Map<String, Object> props; //소셜 로그인 정보

    public MemberSecurityDTO(String username, String password, String email, String name, String phone, String address, String birth, String gender, String mbti, boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.birth = birth;
        this.gender = gender;
        this.mbti = mbti;
        this.del = del;
        this.social = social;

    }

    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid;
    }

}
