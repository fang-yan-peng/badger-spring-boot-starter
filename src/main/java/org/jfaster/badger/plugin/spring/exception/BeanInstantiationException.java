package org.jfaster.badger.plugin.spring.exception;

/**
 *
 * @author yanpengfang
 * create 2019-03-22 3:15 PM
 */
public class BeanInstantiationException extends RuntimeException {

    private Class beanClass;

    public BeanInstantiationException(Class beanClass, String msg) {
        this(beanClass, msg, null);
    }

    public BeanInstantiationException(Class beanClass, String msg, Throwable cause) {
        super("Could not instantiate bean class [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
    }

    public Class getBeanClass() {
        return beanClass;
    }

}