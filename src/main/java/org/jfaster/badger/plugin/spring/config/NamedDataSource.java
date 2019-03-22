package org.jfaster.badger.plugin.spring.config;

import org.jfaster.badger.util.Strings;

/**
 * @author fangyanpeng.
 */
public enum NamedDataSource {

    MASTER("master"), SLAVE("slave");

    private String name;

    NamedDataSource(String name) {
        this.name = name;
    }

    public static boolean isMaster(String name) {
        return MASTER.name.equalsIgnoreCase(name);
    }

    public static boolean isSlave(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return false;
        }
        return name.startsWith(SLAVE.name);
    }

}
