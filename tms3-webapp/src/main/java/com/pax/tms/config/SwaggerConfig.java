package com.pax.tms.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = {"com.pax.tms.open.api.controller"})
public class SwaggerConfig {

    @Bean
    public Docket customDocket() {

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().build()
                .globalOperationParameters(globalOperationParameters())
                .tags(new Tag("Group", ""))
                .tags(new Tag("Terminal", ""))
                .tags(new Tag("Deployment", ""))
                .tags(new Tag("Logging", ""))
                .useDefaultResponseMessages(false).forCodeGeneration(true);
    }

    private List<Parameter> globalOperationParameters() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameters = new ArrayList<>();
        parameterBuilder.name("app_key").description("the key to access ppm api").modelRef(new ModelRef("String"))
                .parameterType("header").required(false).build();
        parameters.add(parameterBuilder.build());
        return parameters;

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("ppm swagger api").description("the document of ppm open api").version("1.0")
                .build();
    }

}
