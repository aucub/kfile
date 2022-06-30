package com.example.kfile.utils;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;

public class FastAutoGenerator {
    public static void main(String[] args) {
        com.baomidou.mybatisplus.generator.FastAutoGenerator.create("jdbc:mysql://", "", "")
                .globalConfig(builder -> builder.author("aucub") // 设置作者
                        .outputDir("output"))
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.example.kfile"); // 设置父包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user", "authority", "share", "file_item") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
