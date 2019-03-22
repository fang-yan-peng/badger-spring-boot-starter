package org.jfaster.badger.plugin.spring.config;

import java.util.List;

/**
 * @author fangyanpeng.
 */
public class BadgerDataSourceConfig {

    private String name;

    private BadgerHikaricpConfig master;

    private List<BadgerHikaricpConfig> slaves;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BadgerHikaricpConfig getMaster() {
        return master;
    }

    public void setMaster(BadgerHikaricpConfig master) {
        this.master = master;
    }

    public List<BadgerHikaricpConfig> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<BadgerHikaricpConfig> slaves) {
        this.slaves = slaves;
    }
}
