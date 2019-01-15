//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dangdang.ddframe.rdb.sharding.routing.type.simple;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataNode;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.constant.SQLType;
import com.dangdang.ddframe.rdb.sharding.hint.HintManagerHolder;
import com.dangdang.ddframe.rdb.sharding.hint.ShardingKey;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.context.condition.Column;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.context.condition.Condition;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.SQLStatement;
import com.dangdang.ddframe.rdb.sharding.routing.type.RoutingEngine;
import com.dangdang.ddframe.rdb.sharding.routing.type.RoutingResult;
import com.dangdang.ddframe.rdb.sharding.routing.type.TableUnit;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.ConstructorProperties;
import java.util.*;

public final class SimpleRoutingEngine implements RoutingEngine {
    private static final Logger log = LoggerFactory.getLogger(SimpleRoutingEngine.class);
    private final ShardingRule shardingRule;
    private final List<Object> parameters;
    private final String logicTableName;
    private final SQLStatement sqlStatement;

    public RoutingResult route() {
        TableRule tableRule = this.shardingRule.getTableRule(this.logicTableName);
        Collection routedDataSources = this.routeDataSources(tableRule);
        Collection routedTables = this.routeTables(tableRule, routedDataSources);
        return this.generateRoutingResult(tableRule, routedDataSources, routedTables);
    }

    private Collection<String> routeDataSources(TableRule tableRule) {
        DatabaseShardingStrategy strategy = this.shardingRule.getDatabaseShardingStrategy(tableRule);
        List shardingValues = HintManagerHolder.isUseShardingHint()?this.getDatabaseShardingValuesFromHint(strategy.getShardingColumns()):this.getShardingValues(strategy.getShardingColumns());
        this.logBeforeRoute("database", this.logicTableName, tableRule.getActualDatasourceNames(), strategy.getShardingColumns(), shardingValues);
        Collection result = strategy.doStaticSharding(this.sqlStatement.getType(), tableRule.getActualDatasourceNames(), shardingValues);
        this.logAfterRoute("database", this.logicTableName, result);
        Preconditions.checkState(!result.isEmpty(), "no database route info");
        return result;
    }

    /**
     * 动态判断分库分表参数 wp
     * @param tableRule
     * @param routedDataSources
     * @return
     */
    private Collection<String> routeTables(TableRule tableRule, Collection<String> routedDataSources) {
        TableShardingStrategy strategy = this.shardingRule.getTableShardingStrategy(tableRule);
        List shardingValues = HintManagerHolder.isUseShardingHint()?this.getTableShardingValuesFromHint(strategy.getShardingColumns()):this.getShardingValues(strategy.getShardingColumns());
        this.logBeforeRoute("table", this.logicTableName, tableRule.getActualTables(), strategy.getShardingColumns(), shardingValues);
//        Collection result = tableRule.isDynamic()?strategy.doDynamicSharding(shardingValues):strategy.doStaticSharding(this.sqlStatement.getType(), tableRule.getActualTableNames(routedDataSources), shardingValues);
        Collection result = strategy.doStaticSharding(this.sqlStatement.getType(), tableRule.getActualTableNames(routedDataSources), shardingValues);
        this.logAfterRoute("table", this.logicTableName, result);
        Preconditions.checkState(!result.isEmpty(), "no table route info");
        return result;
    }

    private List<ShardingValue<?>> getDatabaseShardingValuesFromHint(Collection<String> shardingColumns) {
        ArrayList result = new ArrayList(shardingColumns.size());
        Iterator i$ = shardingColumns.iterator();

        while(i$.hasNext()) {
            String each = (String)i$.next();
            Optional shardingValue = HintManagerHolder.getDatabaseShardingValue(new ShardingKey(this.logicTableName, each));
            if(shardingValue.isPresent()) {
                result.add(shardingValue.get());
            }
        }

        return result;
    }

    private List<ShardingValue<?>> getTableShardingValuesFromHint(Collection<String> shardingColumns) {
        ArrayList result = new ArrayList(shardingColumns.size());
        Iterator i$ = shardingColumns.iterator();

        while(i$.hasNext()) {
            String each = (String)i$.next();
            Optional shardingValue = HintManagerHolder.getTableShardingValue(new ShardingKey(this.logicTableName, each));
            if(shardingValue.isPresent()) {
                result.add(shardingValue.get());
            }
        }

        return result;
    }

    private List<ShardingValue<?>> getShardingValues(Collection<String> shardingColumns) {
        ArrayList result = new ArrayList(shardingColumns.size());
        Iterator i$ = shardingColumns.iterator();

        while(i$.hasNext()) {
            String each = (String)i$.next();
            Optional condition = this.sqlStatement.getConditions().find(new Column(each, this.logicTableName));
            if(condition.isPresent()) {
                result.add(((Condition)condition.get()).getShardingValue(this.parameters));
            }
        }
        return result;
    }

    private void logBeforeRoute(String type, String logicTable, Collection<?> targets, Collection<String> shardingColumns, List<ShardingValue<?>> shardingValues) {
        log.debug("Before {} sharding {} routes db names: {} sharding columns: {} sharding values: {}", new Object[]{type, logicTable, targets, shardingColumns, shardingValues});
    }

    private void logAfterRoute(String type, String logicTable, Collection<String> shardingResults) {
        log.debug("After {} sharding {} result: {}", new Object[]{type, logicTable, shardingResults});
    }

    /**
     * 修改查询数据的时候二次进行动态组合改为以起始datanode为准 wp
     * @param tableRule
     * @param routedDataSources
     * @param routedTables
     * @return
     */
    private RoutingResult generateRoutingResult(TableRule tableRule, Collection<String> routedDataSources, Collection<String> routedTables) {
        RoutingResult result = new RoutingResult();
        if(sqlStatement.getType().equals(SQLType.INSERT)){
            Iterator i$ = tableRule.getActualDataNodes(routedDataSources, routedTables).iterator();
            while(i$.hasNext()) {
                DataNode each = (DataNode)i$.next();
                result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
            }
        }else {
            Iterator i$ = tableRule.actualTables.iterator();
            DatabaseShardingStrategy strategy = this.shardingRule.getDatabaseShardingStrategy(tableRule);
            List<ShardingValue<?>> shardingValues = HintManagerHolder.isUseShardingHint()?this.getDatabaseShardingValuesFromHint(strategy.getShardingColumns()):this.getShardingValues(strategy.getShardingColumns());

            while(i$.hasNext()) {
                DataNode each = (DataNode)i$.next();
                if(!shardingValues.isEmpty() && shardingValues.size()>0){
                    for(ShardingValue shardingValue:shardingValues){
//                        TODO 不支持 between分片策略
//                        对=分片策略处理
                      if((shardingValue.getValue() != null) && ((shardingValue.getValues() == null) || (shardingValue.getValues().size() == 0))){
                          buildTableUnitList(result, each, shardingValue);
//                          对in分片策略处理
                      }else if((shardingValue.getValue() == null) && ((shardingValue.getValues() != null) && (shardingValue.getValues().size() > 0))){
                          for (int i=0;i<shardingValue.getValues().size();i++) {
                              shardingValue.setValue((Comparable<?>) shardingValue.getValues().toArray()[i]);
                              buildTableUnitList(result, each, shardingValue);
                          }
                          shardingValue.setValue(null);
                      }
                    }
                }else {
                    result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
                }
            }
        }
        return result;
    }

    /**
     * 根据不同的分片策略构建tableUnitList
     * @param result
     * @param each
     * @param shardingValue
     */
    private void buildTableUnitList(RoutingResult result, DataNode each, ShardingValue shardingValue) {
        if (shardingValue.getColumnName().equals("company_code")){
           if(((String)shardingValue.getValue()).indexOf("$") == -1){
               if(each.getDataSourceName().endsWith(((String)shardingValue.getValue()).replace("-","_"))){
                   result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
               }else if("38-0001".equals((String)shardingValue.getValue())){
                   result.getTableUnits().getTableUnits().add(new TableUnit("finance_05_0001", this.logicTableName, each.getTableName()));
               }
           }else if(((String)shardingValue.getValue()).indexOf("$") != -1){
               if("finance_00_0001".equals(each.getDataSourceName())){
                   result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
               }
           }
        }else if (shardingValue.getColumnName().equals("package_id")){
            if(each.getDataSourceName().endsWith("01_0001") || each.getDataSourceName().endsWith("10_0001")){
                result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
            }
        }else {
            result.getTableUnits().getTableUnits().add(new TableUnit(each.getDataSourceName(), this.logicTableName, each.getTableName()));
        }
    }

    @ConstructorProperties({"shardingRule", "parameters", "logicTableName", "sqlStatement"})
    public SimpleRoutingEngine(ShardingRule shardingRule, List<Object> parameters, String logicTableName, SQLStatement sqlStatement) {
        this.shardingRule = shardingRule;
        this.parameters = parameters;
        this.logicTableName = logicTableName;
        this.sqlStatement = sqlStatement;
    }
}
