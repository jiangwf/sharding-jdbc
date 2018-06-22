//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dangdang.ddframe.rdb.sharding.api.rule;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public final class DataSourceRule {
    private final Map<String, DataSource> dataSourceMap;
    private final String defaultDataSourceName;

    public DataSourceRule(Map<String, DataSource> dataSourceMap) {
        this(dataSourceMap, (String)null);
    }

    public DataSourceRule(Map<String, DataSource> dataSourceMap, String defaultDataSourceName) {
        Preconditions.checkState(!dataSourceMap.isEmpty(), "Must have one data source at least.");
        this.dataSourceMap = dataSourceMap;
        if(1 == dataSourceMap.size()) {
            this.defaultDataSourceName = (String)((Entry)dataSourceMap.entrySet().iterator().next()).getKey();
        } else if(Strings.isNullOrEmpty(defaultDataSourceName)) {
            this.defaultDataSourceName = null;
        } else {
            Preconditions.checkState(dataSourceMap.containsKey(defaultDataSourceName), "Data source rule must include default data source.");
            this.defaultDataSourceName = defaultDataSourceName;
        }
    }

    public DataSource getDataSource(String name) {
        return (DataSource)this.dataSourceMap.get(name);
    }

    public Optional<DataSource> getDefaultDataSource() {
        return Optional.fromNullable(this.dataSourceMap.get(this.defaultDataSourceName));
    }

    public Collection<String> getDataSourceNames() {
        return this.dataSourceMap.keySet();
    }

    public Collection<DataSource> getDataSources() {
        return this.dataSourceMap.values();
    }

    public String getDefaultDataSourceName() {
        return this.defaultDataSourceName;
    }

    public Map<String, DataSource> getDataSource(){
        return dataSourceMap;
    }
}
