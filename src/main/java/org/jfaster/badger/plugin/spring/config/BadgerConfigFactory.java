package org.jfaster.badger.plugin.spring.config;

import java.util.Iterator;
import java.util.Map;

import org.jfaster.badger.plugin.spring.exception.BadgerAutoConfigException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author yanpengfang.
 */
public class BadgerConfigFactory {

    /**
     * 获取配置
     *
     * @param beanFactory beanFactory
     * @param prefix 前缀
     * @return
     */

    public static BadgerConfig getBadgerConfig(DefaultListableBeanFactory beanFactory,String prefix){
        BadgerConfig target = new BadgerConfig();
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(target);
        factory.setPropertySources(deducePropertySources(beanFactory));
        factory.setConversionService(new DefaultConversionService());
        factory.setIgnoreInvalidFields(false);
        factory.setIgnoreUnknownFields(true);
        factory.setIgnoreNestedProperties(false);
        factory.setTargetName(prefix);
        try {
            factory.bindPropertiesToTarget();
        }
        catch (Exception ex) {
            throw new BadgerAutoConfigException(ex);
        }
        return target;
    }


    private static PropertySources deducePropertySources(DefaultListableBeanFactory beanFactory) {
        PropertySourcesPlaceholderConfigurer configurer = getSinglePropertySourcesPlaceholderConfigurer(beanFactory);
        if (configurer != null) {
            return new FlatPropertySources(configurer.getAppliedPropertySources());
        }
        Environment environment = new StandardEnvironment();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        return new FlatPropertySources(propertySources);

    }

    private static PropertySourcesPlaceholderConfigurer getSinglePropertySourcesPlaceholderConfigurer(DefaultListableBeanFactory beanFactory) {
        Map<String, PropertySourcesPlaceholderConfigurer> beans = beanFactory.getBeansOfType(PropertySourcesPlaceholderConfigurer.class, false, false);
        if (beans.size() == 1) {
            return beans.values().iterator().next();
        }
        return null;
    }

    private static class FlatPropertySources implements PropertySources {

        private PropertySources propertySources;

        FlatPropertySources(PropertySources propertySources) {
            this.propertySources = propertySources;
        }

        @Override
        public Iterator<PropertySource<?>> iterator() {
            MutablePropertySources result = getFlattened();
            return result.iterator();
        }

        @Override
        public boolean contains(String name) {
            return get(name) != null;
        }

        @Override
        public PropertySource<?> get(String name) {
            return getFlattened().get(name);
        }

        private MutablePropertySources getFlattened() {
            MutablePropertySources result = new MutablePropertySources();
            for (PropertySource<?> propertySource : this.propertySources) {
                flattenPropertySources(propertySource, result);
            }
            return result;
        }

        private void flattenPropertySources(PropertySource<?> propertySource, MutablePropertySources result) {
            Object source = propertySource.getSource();
            if (source instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
                for (PropertySource<?> childSource : environment.getPropertySources()) {
                    flattenPropertySources(childSource, result);
                }
            }
            else {
                result.addLast(propertySource);
            }
        }
    }


}
