package com.odc.om.paie.authenticated.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")  // Reference the correct bean name

public class PersistenceConfig {
//
//    @Bean
//    public EntityAuditor entityAuditor(UserService userService) {
//        return new EntityAuditor(userService);
//    }
}

