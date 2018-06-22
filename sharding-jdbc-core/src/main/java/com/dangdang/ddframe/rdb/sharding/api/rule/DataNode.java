//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dangdang.ddframe.rdb.sharding.api.rule;

import com.google.common.base.Splitter;
import java.beans.ConstructorProperties;
import java.util.List;

public class DataNode {
    private static final String DELIMITER = ".";
    private final String dataSourceName;
    private final String tableName;

    public DataNode(String dataNode) {
        List segments = Splitter.on(".").splitToList(dataNode);
        this.dataSourceName = (String)segments.get(0);
        this.tableName = (String)segments.get(1);
    }

    public static boolean isValidDataNode(String dataNodeStr) {
        return dataNodeStr.contains(".") && 2 == Splitter.on(".").splitToList(dataNodeStr).size();
    }

    @ConstructorProperties({"dataSourceName", "tableName"})
    public DataNode(String dataSourceName, String tableName) {
        this.dataSourceName = dataSourceName;
        this.tableName = tableName;
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(!(o instanceof DataNode)) {
            return false;
        } else {
            DataNode other = (DataNode)o;
            if(!other.canEqual(this)) {
                return false;
            } else {
                String this$dataSourceName = this.getDataSourceName();
                String other$dataSourceName = other.getDataSourceName();
                if(this$dataSourceName == null) {
                    if(other$dataSourceName != null) {
                        return false;
                    }
                } else if(!this$dataSourceName.equals(other$dataSourceName)) {
                    return false;
                }

                String this$tableName = this.getTableName();
                String other$tableName = other.getTableName();
                if(this$tableName == null) {
                    if(other$tableName != null) {
                        return false;
                    }
                } else if(!this$tableName.equals(other$tableName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DataNode;
    }

    public int hashCode() {
        boolean PRIME = true;
        byte result = 1;
        String $dataSourceName = this.getDataSourceName();
        int result1 = result * 59 + ($dataSourceName == null?0:$dataSourceName.hashCode());
        String $tableName = this.getTableName();
        result1 = result1 * 59 + ($tableName == null?0:$tableName.hashCode());
        return result1;
    }

    public String toString() {
        return "DataNode(dataSourceName=" + this.getDataSourceName() + ", tableName=" + this.getTableName() + ")";
    }
}
