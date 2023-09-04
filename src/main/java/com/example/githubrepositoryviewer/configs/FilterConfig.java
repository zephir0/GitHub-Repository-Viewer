package com.example.githubrepositoryviewer.configs;

import com.example.githubrepositoryviewer.configs.filters.AcceptHeaderCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AcceptHeaderCheckFilter> loggingFilter() {
        FilterRegistrationBean<AcceptHeaderCheckFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AcceptHeaderCheckFilter());
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }
}
