package org.jfaster.badger.plugin.spring.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author  fangyanpeng.
 */
@Configuration
@ConditionalOnClass({BadgerDaoAutoCreator.class})
public class BadgerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BadgerDaoAutoCreator.class)
    public BadgerDaoAutoCreator autoCreator(){
        return new BadgerDaoAutoCreator();
    }
}
