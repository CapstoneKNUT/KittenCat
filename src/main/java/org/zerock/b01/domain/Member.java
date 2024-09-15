package org.zerock.b01.domain;


import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity{

    @Id
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

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    public void changePassword(String mpw ){
        this.mpw = mpw;
    }

    public void changeEmail(String email){
        this.email = email;
    }
    public void changePhone(String phone){
        this.phone = phone;
    }
    public void changeAddress(String address){
        this.address = address;
    }
    public void changeBirth(String birth){
        this.birth = birth;
    }
    public void changeMbti(String mbti){
        this.mbti = mbti;
    }

    public void changeDel(boolean del){
        this.del = del;
    }

    public void addRole(MemberRole memberRole){
        this.roleSet.add(memberRole);
    }

    public void clearRoles() {
        this.roleSet.clear();
    }

    public void changeSocial(boolean social){this.social = social;}
}
