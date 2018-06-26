package com.dangdang.sharding.test.util;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 功能说明：实现finance亏分片策略
 *
 * @author:weifeng.jiang
 * @DATE:2017/7/27 @TIME:18:15
 */
public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String>{

    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
       for(String databaseName:availableTargetNames){
           if(databaseName.endsWith(shardingValue.getValue())){
               return databaseName;
           }
       }
        throw new BusException("=分片没有路由到数据库，分片字段值={}"+shardingValue.getValue());
    }

    @Override
    public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
       Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Collection<String> values = shardingValue.getValues();
        for (String companyCode:values){
            for (String databaseName:availableTargetNames){
                if(databaseName.endsWith(companyCode)){
                    result.add(databaseName);
                }
            }
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
        throw new BusException("不支持between分片规则");
    }
}
