package org.zerock.b01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration

public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                //Swagger에서 제공하는 기본 응답 메시지를 사용하지 않도록 설정합니다.
                .select()   //ApiSelectorBuilder를 반환하여 API 선택을 세부적으로 설정할 수 있게 합니다.
                .apis(RequestHandlerSelectors.basePackage("org.zerock.b01.controller"))
                //패키지 내의 모든 컨트롤러를 대상으로 API 문서를 생성합니다.
                .paths(PathSelectors.any())
                //모든 경로에 대해 API 문서를 생성합니다.
                .build()
                .apiInfo(apiInfo());
                //API 문서에 대한 기본 정보를 설정합니다.
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder() //ApiInfoBuilder: API 정보 생성을 도와주는 빌더 클래스입니다.
                .title("Boot 01 Project Swagger")
                //title("Boot 01 Project Swagger"): API 문서의 제목을 설정합니다.
                .build();
    }
}
