package com.example.dynamicdatasource;

import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/20 17:04
 * @description : 单库分表操作
 */
public class ModuloShardingTableAlgorithm2 implements ComplexKeysShardingAlgorithm<Integer> {


    public static void main(String[] args) throws Exception {
        DataSource dataSource = new ModuloShardingTableAlgorithm2().getShardingDataSource();
        String sql = "insert into t_order (id,name ,user_id) values (?, ?,?)";
        Connection connection = dataSource.getConnection();
        for (int i = 0; i < 10; i++) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 2 * i);
            preparedStatement.setString(2, "ss" + i);
            preparedStatement.setInt(3, i + 2);
            preparedStatement.execute();
            connection.close();
        }
    }

    DataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        shardingRuleConfig.getBroadcastTables().add("t_config");
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new NoneShardingStrategyConfiguration());

        //同一个库分表
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration("user_id", new ModuloShardingTableAlgorithm2()));
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
    }

    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration() {
        KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("SNOWFLAKE", "order_id");
        return result;
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order", "ds0.t_order${0..1}");
        // result.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
        return result;
    }

    TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item", "ds0.t_order_item${0..1}");
        return result;
    }

    Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds0", DataSourceUtil.createDataSource("ds0"));
        return result;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Integer> shardingValue) {
        Integer size = availableTargetNames.size();
        // 获取表
        return getTables(shardingValue, size);
    }

    int i = 0;

    private Set<String> getTables(ComplexKeysShardingValue<Integer> complexKeysShardingValue, Integer size) {
        Set<String> tables = new HashSet<>();
        // 分页条件 , 包含了多个分片键的值
        Map<String, Collection<Integer>> map = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        map.forEach((k, v) -> {
            // 获取Value
            v.forEach(value -> {
                System.out.println("当前user_id:" + value + "  i:" + i);
                if (i++ % 2 == 0) {
                    tables.add("t_order0");
                } else {
                    tables.add("t_order1");

                }
            });

        });
        return tables;
    }
}

