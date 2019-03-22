package org.jfaster.badger.plugin.spring.starter;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jfaster.badger.Badger;
import org.jfaster.badger.jdbc.datasource.DataSourceFactory;
import org.jfaster.badger.jdbc.datasource.support.AbstractDataSourceFactory;
import org.jfaster.badger.jdbc.datasource.support.MasterSlaveDataSourceFactory;
import org.jfaster.badger.jdbc.datasource.support.SingleDataSourceFactory;
import org.jfaster.badger.plugin.spring.config.BadgerConfig;
import org.jfaster.badger.plugin.spring.config.BadgerConfigFactory;
import org.jfaster.badger.plugin.spring.config.BadgerDataSourceConfig;
import org.jfaster.badger.plugin.spring.config.BadgerHikaricpConfig;
import org.jfaster.badger.plugin.spring.exception.BadgerAutoConfigException;
import org.jfaster.badger.plugin.spring.utils.Reflections;
import org.jfaster.badger.sql.interceptor.SqlInterceptor;
import org.jfaster.badger.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author fangyanpeng.
 */
public class BadgerDaoAutoCreator implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static final String PREFIX = "badger";

    private ApplicationContext context;

    private BadgerConfig config;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        config = BadgerConfigFactory.getBadgerConfig(beanFactory, PREFIX);
        if (config == null) {
            throw new BadgerAutoConfigException("Badger config file does not exist!");
        }

        BeanDefinitionBuilder badgerDefinition = BeanDefinitionBuilder.rootBeanDefinition(Badger.class);
        badgerDefinition.setFactoryMethod("newInstance");
        if (!Strings.isNullOrEmpty(config.getDialect())) {
            badgerDefinition.addPropertyValue("dialect", config.getDialect());
        }
        if (config.getCacheSqlLimit() > 0) {
            badgerDefinition.addPropertyValue("cacheSqlLimit", config.getCacheSqlLimit());
        }
        if (config.getPageSizeLimit() > 0) {
            badgerDefinition.addPropertyValue("pageSizeLimit", config.getPageSizeLimit());
        }
        if (config.getQueryTimeout() > 0) {
            badgerDefinition.addPropertyValue("queryTimeout", config.getQueryTimeout());
        }
        if (!Strings.isNullOrEmpty(config.getTransactionManager())) {
            badgerDefinition.addPropertyValue("transactionManager", config.getTransactionManager());
        }
        if (!Strings.isNullOrEmpty(config.getInterceptorClass())) {
            try {
                Class<?> interceptorClz = Class.forName(config.getInterceptorClass());
                if (!SqlInterceptor.class.isAssignableFrom(interceptorClz)) {
                    throw new BadgerAutoConfigException(config.getInterceptorClass() + " 没有实现 org.jfaster.badger.sql.interceptor.SqlInterceptor接口");
                }
                SqlInterceptor sqlInterceptor = (SqlInterceptor) Reflections.instantiateClass(interceptorClz);
                badgerDefinition.addPropertyValue("interceptor", sqlInterceptor);
            } catch (Throwable e) {
                throw new IllegalStateException("初始化sql拦截器失败", e);
            }
        } else if (!Strings.isNullOrEmpty(config.getInterceptorRef())) {
            badgerDefinition.addPropertyReference("interceptor", config.getInterceptorRef());
        }
        configBadgerDatasourceFactory(badgerDefinition);
        beanFactory.registerBeanDefinition(Badger.class.getName(), badgerDefinition.getBeanDefinition());
    }

    /**
     * 设置datasource
     * @param badgerDefinition
     */
    private void configBadgerDatasourceFactory(BeanDefinitionBuilder badgerDefinition) {
        List<DataSourceFactory> dataSourceFactories = new ArrayList<>();
        for (BadgerDataSourceConfig dataSourceConfig : config.getDatasources()) {
            String name = dataSourceConfig.getName();
            BadgerHikaricpConfig masterConfig = dataSourceConfig.getMaster();
            List<BadgerHikaricpConfig> slaveConfigs = dataSourceConfig.getSlaves();
            if (masterConfig == null) {
                throw new BadgerAutoConfigException("Does not exist master datasource");
            }
            if (Strings.isNullOrEmpty(name)) {
                name = AbstractDataSourceFactory.DEFULT_NAME;
            }
            DataSourceFactory dataSourceFactory;
            DataSource masterDataSource = getDataSource(masterConfig);
            if (slaveConfigs == null || slaveConfigs.isEmpty()) {
                dataSourceFactory = new SingleDataSourceFactory(name, masterDataSource);
            } else {
                List<DataSource> slaves = new ArrayList<>(slaveConfigs.size());
                for (BadgerHikaricpConfig hikaricpConfig : slaveConfigs) {
                    slaves.add(getDataSource(hikaricpConfig));
                }
                dataSourceFactory = new MasterSlaveDataSourceFactory(name, masterDataSource, slaves);
            }
            dataSourceFactories.add(dataSourceFactory);
        }
        if (dataSourceFactories.isEmpty()) {
            throw new BadgerAutoConfigException("Badger 没有配置任何数据源相关的信息");
        }
        badgerDefinition.addPropertyValue("dataSourceFactories", dataSourceFactories);
    }

    private DataSource getDataSource(BadgerHikaricpConfig dataSourceConfig) {
        if (!Strings.isNullOrEmpty(dataSourceConfig.getRef())) {
            return context.getBean(dataSourceConfig.getRef(), DataSource.class);
        }
        return new HikariDataSource(dataSourceConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
