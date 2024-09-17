package org.example.tokpik_be.config;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.common.DevAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final DevAuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/error");
    }
}
