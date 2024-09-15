package org.zerock.b01.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.security.dto.MemberSecurityDTO;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("userRequest....");
        log.info(userRequest);

        log.info("oauth2 user.....................................");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("NAME: "+clientName);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;
        String name = null;
        String gender = null;
        String birth = null;
        String phone = null;

        switch (clientName){
            case "kakao":
                Map<String, String> kakaoInfo = getKakaoInfo(paramMap);
                email = kakaoInfo.get("email");
                gender = kakaoInfo.get("gender");
                birth = kakaoInfo.get("birth");
                name = kakaoInfo.get("name");
                phone = kakaoInfo.get("phoneNumber");
                break;
        }

        log.info("===============================");
        log.info(email);
        log.info(gender);
        log.info(birth);
        log.info(name);
        log.info(phone);
        log.info("===============================");

        return generateDTO(email, gender, birth, name, phone, paramMap);
    }

    private MemberSecurityDTO generateDTO(String email, String gender, String birth, String name, String phone, Map<String, Object> params){

        Optional<Member> result = memberRepository.findByEmail(email);

        //데이터베이스에 해당 이메일을 사용자가 없다면
        if(result.isEmpty()){
            //회원 추가 -- mid는 이메일 주소/ 패스워드는 1111
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .name(name)
                    .phone(phone)
                    .gender(gender)
                    .birth(birth)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            //MemberSecurityDTO 구성 및 반환
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email, "1111", email, name, phone, null, birth, gender, null,false, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        }else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getMpw(),
                            member.getEmail(),
                            member.getName(),
                            member.getPhone(),
                            member.getAddress(),
                            member.getBirth(),
                            member.getGender(),
                            member.getMbti(),
                            member.isDel(),
                            member.isSocial(),
                            member.getRoleSet()
                                    .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
                                    .collect(Collectors.toList())
                    );

            return memberSecurityDTO;
        }
    }


    private Map<String, String> getKakaoInfo(Map<String, Object> paramMap){

        log.info("KAKAO-----------------------------------------");

        Object value = paramMap.get("kakao_account");

        log.info(value);

        LinkedHashMap<String, Object> accountMap = (LinkedHashMap<String, Object>) value;

        String email = (String)accountMap.get("email");
        String name = (String)accountMap.get("name");
        String phoneNumber = (String)accountMap.get("phone_number");
        String gender = (String)accountMap.get("gender");
        String birthDay = (String)accountMap.get("birthDay");
        String birthyear = (String)accountMap.get("birthyear");

        log.info("email..." + email);
        log.info("gender..." + gender);
        log.info("birth..." + birthyear + birthDay);
        log.info("name..." + name);
        log.info("phone..." + phoneNumber);

        Map<String, String> kakaoInfo = new LinkedHashMap<>();

        kakaoInfo.put("email", email);
        kakaoInfo.put("gender", gender);
        kakaoInfo.put("birth", birthyear + birthDay);
        kakaoInfo.put("name", name);
        kakaoInfo.put("phoneNumber", phoneNumber);
        return kakaoInfo;
    }

}

