//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dangdang.ddframe.rdb.sharding.api.rule;

import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.keygen.KeyGenerator;
import com.dangdang.ddframe.rdb.sharding.keygen.KeyGeneratorFactory;
import com.dangdang.ddframe.rdb.sharding.util.Constant;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.ConstructorProperties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TableRule {

    private static final Logger logger = LoggerFactory.getLogger(TableRule.class);
    public String logicTable;
    public boolean dynamic = false;
    public List<DataNode> actualTables;
    public List<DataNode> actualTablesBack;
    public DatabaseShardingStrategy databaseShardingStrategy;
    public TableShardingStrategy tableShardingStrategy;
    public String generateKeyColumn;
    public KeyGenerator keyGenerator;

    /** @deprecated  增加程序启动动态解析分库分表datanode参数 wp*/
    @Deprecated
    public TableRule(String logicTable, boolean dynamic, List<String> actualTables, DataSourceRule dataSourceRule, Collection<String> dataSourceNames, DatabaseShardingStrategy databaseShardingStrategy, TableShardingStrategy tableShardingStrategy, String generateKeyColumn, KeyGenerator keyGenerator) {
        Preconditions.checkNotNull(logicTable);
        this.logicTable = logicTable;
        this.dynamic = dynamic;
        this.databaseShardingStrategy = databaseShardingStrategy;
        this.tableShardingStrategy = tableShardingStrategy;
        if(dynamic) {
//            Preconditions.checkNotNull(dataSourceRule);
            this.actualTables = this.generateDataNodes(actualTables, dataSourceRule);
            this.actualTablesBack = this.actualTables;

            Map<String,String> yearMonthMap = new LinkedHashMap<>();
            try {
                Date dateFrom = new SimpleDateFormat("yyyyMM").parse("201707");//定义起始日期
                Date dateEnd = new SimpleDateFormat("yyyyMM").parse(new SimpleDateFormat("yyyyMM").format(new Date()));//定义结束日期
                Calendar calBegin = Calendar.getInstance();
                calBegin.setTime(dateFrom);
                Calendar calEnd = Calendar.getInstance();
                calEnd.setTime(dateEnd);
                while (calEnd.after(calBegin)) {
                    // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                    calBegin.add(Calendar.MONTH, 1);
                    yearMonthMap.put((String)(new SimpleDateFormat("yyyyMM")).format(calBegin.getTime()), "");
                }
                boolean flag = false;
                // 201707@finance_01_001-fiancne_10_001
                List<DataNode> listDataNode = new ArrayList();
                for (String dataNode:actualTables){
                    for(String dataBase:dataNode.split("-")){
                        if (dataBase.contains("@")){
                            String[] str = dataBase.split("@");
                            Map<String,String> yearMonthMapTemp = new LinkedHashMap<>();
                            try {
                                if (StringUtils.isNumeric(str[0])){
                                    Date dateFromTemp = new SimpleDateFormat("yyyyMM").parse(str[0]);//定义起始日期
                                    Calendar calBeginTemp = Calendar.getInstance();
                                    calBeginTemp.setTime(dateFromTemp);
                                    Calendar calEndTemp = Calendar.getInstance();
                                    calEndTemp.setTime(dateEnd);
                                    while (calEndTemp.after(calBeginTemp)) {
                                        // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                                        calBeginTemp.add(Calendar.MONTH, 1);
                                        yearMonthMapTemp.put((String)(new SimpleDateFormat("yyyyMM")).
                                                format(calBeginTemp.getTime()), "");
                                    }
                                }else {
                                    throw new RuntimeException("分库分表动态参数配置错误（actual-tables），日期年月配置错误无法解析");
                                }
                                flag = splitTable(str[1], listDataNode, yearMonthMapTemp, logicTable, dataSourceRule);
                            }catch (Exception e){
                                throw new RuntimeException("分库分表动态参数配置错误（actual-tables），程序无法解析"+e.getMessage());
                            }finally {
                                yearMonthMapTemp.clear();
                            }
                        }else {
                            flag = splitTable(dataBase, listDataNode, yearMonthMap, logicTable, dataSourceRule);
                        }
                        if (!flag){
                            break;
                        }
                    }
                }
                if (flag){
                    this.actualTables = listDataNode;
                }else {
                    this.actualTables = null;
                }
            } catch (ParseException e) {
                logger.error("转换分库分表月份配置失败"+e.getMessage());
                throw new RuntimeException("转换分库分表月份配置失败"+e.getMessage());
            }

        } else if(null != actualTables && !actualTables.isEmpty()) {
            this.actualTables = this.generateDataNodes(actualTables, dataSourceRule, dataSourceNames);
            this.actualTablesBack = this.actualTables;
        } else {
            Preconditions.checkNotNull(dataSourceRule);
            this.actualTables = this.generateDataNodes(Collections.singletonList(logicTable), dataSourceRule, dataSourceNames);
            this.actualTablesBack = this.actualTables;
        }

        this.generateKeyColumn = generateKeyColumn;
        this.keyGenerator = keyGenerator;
    }

    public List<DataNode> getActualTablesBack() {
        return actualTablesBack;
    }

    public void setActualTablesBack(List<DataNode> actualTablesBack) {
        this.actualTablesBack = actualTablesBack;
    }


    /**
     * 动态构建逻辑表和对应月份数据为物理表DataNode对象数据 wp
     * @param dataBase
     * @param listDataNode
     * @param yearMonthMap
     * @param logicTable
     * @return
     */
    public static boolean splitTable(String dataBase, List listDataNode, Map<String,String> yearMonthMap, String logicTable,DataSourceRule dataSourceRule){
        boolean flag = true;
        if (dataBase != Constant.DATABASE_NAME && dataSourceRule.getDataSource(dataBase) != null){
            for (Map.Entry<String, String> entry : yearMonthMap.entrySet()) {
                DataNode dataNodeNew = new DataNode(dataBase,logicTable+"_"+entry.getKey());
                listDataNode.add(dataNodeNew);
            }
        }else {
            flag = false;
            throw new RuntimeException("分库分表动态参数配置错误（actual-tables），程序无法解析,数据源："+dataBase+" 不存在或者配置错误");
        }
        return flag;
    }


    public static TableRule.TableRuleBuilder builder(String logicTable) {
        return new TableRule.TableRuleBuilder(logicTable);
    }

    /**
     * 表动态取值默认取配置中的参数暂不处理待后面处理 wp
     * @param actualTables
     * @param dataSourceRule
     * @return
     */
    private List<DataNode> generateDataNodes(List<String> actualTables, DataSourceRule dataSourceRule) {
        Collection dataSourceNames = dataSourceRule.getDataSourceNames();
        ArrayList result = new ArrayList(dataSourceNames.size());
//        Iterator i$ = dataSourceNames.iterator();
//        while(i$.hasNext()) {
//            String each = (String)i$.next();
//            result.add(new DynamicDataNode(each));
//        }

        Iterator i$ = actualTables.iterator();
        while(true) {
            while (i$.hasNext()) {
                String actualTable = (String) i$.next();
                result.add(new DataNode(logicTable,actualTable));
            }
            return result;
        }
    }

    private List<DataNode> generateDataNodes(List<String> actualTables, DataSourceRule dataSourceRule, Collection<String> actualDataSourceNames) {
        Collection dataSourceNames = this.getDataSourceNames(dataSourceRule, actualDataSourceNames);
        ArrayList result = new ArrayList(actualTables.size() * (dataSourceNames.isEmpty()?1:dataSourceNames.size()));
        Iterator i$ = actualTables.iterator();

        while(true) {
            while(i$.hasNext()) {
                String actualTable = (String)i$.next();
                if(DataNode.isValidDataNode(actualTable)) {
                    result.add(new DataNode(actualTable));
                } else {
                    Iterator i$1 = dataSourceNames.iterator();

                    while(i$1.hasNext()) {
                        String dataSourceName = (String)i$1.next();
                        result.add(new DataNode(dataSourceName, actualTable));
                    }
                }
            }
            return result;
        }
    }

    private Collection<String> getDataSourceNames(DataSourceRule dataSourceRule, Collection<String> actualDataSourceNames) {
        return (Collection)(null == dataSourceRule?Collections.emptyList():(null != actualDataSourceNames && !actualDataSourceNames.isEmpty()?actualDataSourceNames:dataSourceRule.getDataSourceNames()));
    }

    public Collection<DataNode> getActualDataNodes(Collection<String> targetDataSources, Collection<String> targetTables) {
        return this.dynamic?this.getDynamicDataNodes(targetDataSources, targetTables):this.getStaticDataNodes(targetDataSources, targetTables);
    }

    private Collection<DataNode> getDynamicDataNodes(Collection<String> targetDataSources, Collection<String> targetTables) {
        LinkedHashSet result = new LinkedHashSet(targetDataSources.size() * targetTables.size());
        Iterator i$ = targetDataSources.iterator();

        while(i$.hasNext()) {
            String targetDataSource = (String)i$.next();
            Iterator i$1 = targetTables.iterator();

            while(i$1.hasNext()) {
                String targetTable = (String)i$1.next();
                result.add(new DataNode(targetDataSource, targetTable));
            }
        }
        return result;
    }

    private Collection<DataNode> getStaticDataNodes(Collection<String> targetDataSources, Collection<String> targetTables) {
        LinkedHashSet result = new LinkedHashSet(this.actualTables.size());
        Iterator i$ = this.actualTables.iterator();

        while(i$.hasNext()) {
            DataNode each = (DataNode)i$.next();
            if(targetDataSources.contains(each.getDataSourceName()) && targetTables.contains(each.getTableName())) {
                result.add(each);
            }
        }
        return result;
    }

    public Collection<String> getActualDatasourceNames() {
        LinkedHashSet result = new LinkedHashSet(this.actualTables.size());
        Iterator i$ = this.actualTables.iterator();

        while(i$.hasNext()) {
            DataNode each = (DataNode)i$.next();
            result.add(each.getDataSourceName());
        }
        return result;
    }

    public Collection<String> getActualTableNames(Collection<String> targetDataSources) {
        LinkedHashSet result = new LinkedHashSet(this.actualTables.size());
        Iterator i$ = this.actualTables.iterator();

        while(i$.hasNext()) {
            DataNode each = (DataNode)i$.next();
            if(targetDataSources.contains(each.getDataSourceName())) {
                result.add(each.getTableName());
            }
        }
        return result;
    }

    int findActualTableIndex(String dataSourceName, String actualTableName) {
        int result = 0;

        for(Iterator i$ = this.actualTables.iterator(); i$.hasNext(); ++result) {
            DataNode each = (DataNode)i$.next();
            if(each.getDataSourceName().equalsIgnoreCase(dataSourceName) && each.getTableName().equalsIgnoreCase(actualTableName)) {
                return result;
            }
        }
        return -1;
    }

    public String getLogicTable() {
        return this.logicTable;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public List<DataNode> getActualTables() {
        return this.actualTables;
    }

    public void setActualTables(List<DataNode> actualTables) {
        this.actualTables = actualTables;
    }

    public DatabaseShardingStrategy getDatabaseShardingStrategy() {
        return this.databaseShardingStrategy;
    }

    public TableShardingStrategy getTableShardingStrategy() {
        return this.tableShardingStrategy;
    }

    public String getGenerateKeyColumn() {
        return this.generateKeyColumn;
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    public String toString() {
        return "TableRule(logicTable=" + this.getLogicTable() + ", dynamic=" + this.isDynamic() + ", actualTables=" + this.getActualTables() + ", databaseShardingStrategy=" + this.getDatabaseShardingStrategy() + ", tableShardingStrategy=" + this.getTableShardingStrategy() + ", generateKeyColumn=" + this.getGenerateKeyColumn() + ", keyGenerator=" + this.getKeyGenerator() + ")";
    }

    public static class TableRuleBuilder {
        private String logicTable;
        private boolean dynamic;
        private List<String> actualTables;
        private DataSourceRule dataSourceRule;
        private Collection<String> dataSourceNames;
        private DatabaseShardingStrategy databaseShardingStrategy;
        private TableShardingStrategy tableShardingStrategy;
        private String generateKeyColumn;
        private Class<? extends KeyGenerator> keyGeneratorClass;

        public TableRule.TableRuleBuilder dynamic(boolean dynamic) {
            this.dynamic = dynamic;
            return this;
        }

        public TableRule.TableRuleBuilder actualTables(List<String> actualTables) {
            this.actualTables = actualTables;
            return this;
        }

        public TableRule.TableRuleBuilder dataSourceRule(DataSourceRule dataSourceRule) {
            this.dataSourceRule = dataSourceRule;
            return this;
        }

        public TableRule.TableRuleBuilder dataSourceNames(Collection<String> dataSourceNames) {
            this.dataSourceNames = dataSourceNames;
            return this;
        }

        public TableRule.TableRuleBuilder databaseShardingStrategy(DatabaseShardingStrategy databaseShardingStrategy) {
            this.databaseShardingStrategy = databaseShardingStrategy;
            return this;
        }

        public TableRule.TableRuleBuilder tableShardingStrategy(TableShardingStrategy tableShardingStrategy) {
            this.tableShardingStrategy = tableShardingStrategy;
            return this;
        }

        public TableRule.TableRuleBuilder generateKeyColumn(String generateKeyColumn) {
            this.generateKeyColumn = generateKeyColumn;
            return this;
        }

        public TableRule.TableRuleBuilder generateKeyColumn(String generateKeyColumn, Class<? extends KeyGenerator> keyGeneratorClass) {
            this.generateKeyColumn = generateKeyColumn;
            this.keyGeneratorClass = keyGeneratorClass;
            return this;
        }

        public TableRule build() {
            KeyGenerator keyGenerator = null;
            if(null != this.generateKeyColumn && null != this.keyGeneratorClass) {
                keyGenerator = KeyGeneratorFactory.createKeyGenerator(this.keyGeneratorClass);
            }

            return new TableRule(this.logicTable, this.dynamic, this.actualTables, this.dataSourceRule, this.dataSourceNames, this.databaseShardingStrategy, this.tableShardingStrategy, this.generateKeyColumn, keyGenerator);
        }

        @ConstructorProperties({"logicTable"})
        public TableRuleBuilder(String logicTable) {
            this.logicTable = logicTable;
        }
    }
}
