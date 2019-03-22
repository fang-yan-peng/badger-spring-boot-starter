package org.jfaster.badger.plugin.spring.config;


import java.util.List;

/**
 * @author fangyanpeng.
 */
public class BadgerConfig {

    private String dialect;

    /**
     * 查询超时
     */
    private int queryTimeout;

    /**
     * 设置缓存sql的大小
     */
    private int cacheSqlLimit;

    /**
     * 设置分页数据的每页大小
     */
    private int pageSizeLimit;

    /**
     * 设置是否使用spring的事物管理器
     */
    private String transactionManager;

    private String interceptorClass;

    private String interceptorRef;

    private List<BadgerDataSourceConfig> datasources;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getCacheSqlLimit() {
        return cacheSqlLimit;
    }

    public void setCacheSqlLimit(int cacheSqlLimit) {
        this.cacheSqlLimit = cacheSqlLimit;
    }

    public int getPageSizeLimit() {
        return pageSizeLimit;
    }

    public void setPageSizeLimit(int pageSizeLimit) {
        this.pageSizeLimit = pageSizeLimit;
    }

    public String getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(String transactionManager) {
        this.transactionManager = transactionManager;
    }

    public String getInterceptorClass() {
        return interceptorClass;
    }

    public void setInterceptorClass(String interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    public String getInterceptorRef() {
        return interceptorRef;
    }

    public void setInterceptorRef(String interceptorRef) {
        this.interceptorRef = interceptorRef;
    }

    public List<BadgerDataSourceConfig> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<BadgerDataSourceConfig> datasources) {
        this.datasources = datasources;
    }
}
