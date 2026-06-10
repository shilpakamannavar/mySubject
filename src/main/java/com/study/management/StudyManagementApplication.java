package com.study.management;

import com.study.management.domain.service.FeeCalculatorDomainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.study.management.domain.model")
@EnableJpaRepositories("com.study.management.infrastructure.persistence")
public class StudyManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyManagementApplication.class, args);
    }

    /**
     * FeeCalculatorDomainService has no Spring annotations by design
     * (pure domain object). Expose it as a @Bean so Spring can inject it
     * into PaymentApplicationService.
     */
    @Bean
    public FeeCalculatorDomainService feeCalculatorDomainService() {
        return new FeeCalculatorDomainService();
    }
}
