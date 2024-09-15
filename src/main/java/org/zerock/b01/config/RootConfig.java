package org.zerock.b01.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    @Bean   //객체 간의 매핑을 쉽게 할 수 있도록 도와주는 라이브러리
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()  //ModelMapper의 설정을 가져옵니다
                .setFieldMatchingEnabled(true)  //필드 매칭을 활성화합니다. 이를 통해 필드 이름이 같은 경우 필드 간의 매핑이 가능해집니다.
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)  //접근 수준을 PRIVATE으로 설정하여, private 필드에도 접근할 수 있도록 합니다.
                .setMatchingStrategy(MatchingStrategies.LOOSE); //매칭 전략을 LOOSE로 설정합니다. 이는 더 느슨한 매핑을 허용하여, 이름이 비슷한 필드끼리 매핑될 수 있게 합니다.

        return modelMapper;
    }
}
