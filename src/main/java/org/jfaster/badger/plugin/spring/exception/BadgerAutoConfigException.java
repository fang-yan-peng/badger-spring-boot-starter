package org.jfaster.badger.plugin.spring.exception;

/**
 * @author fangyanpeng.
 */
public class BadgerAutoConfigException extends RuntimeException {

    public BadgerAutoConfigException(Throwable e){
        super(e);
    }

    public BadgerAutoConfigException(Exception e){
        super(e);
    }

    public BadgerAutoConfigException(String message){
        super(message);
    }

    public BadgerAutoConfigException(String format,Object... args){
        super(String.format(format,args));
    }
}
