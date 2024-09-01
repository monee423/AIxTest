package com.henry.springbootinit.model.chart;

import java.io.Serializable;
import lombok.Data;

/**
 * 创建请求
 * @author henry
 */
@Data
public class ChartAddRequest implements Serializable {
    // 分析数据
    private String goal;

    // 图表名称
    private String chartName;

    // 图表数据
    private String chartData;

    // 图表类型
    private String chartType;

    private static final long serialVersionUID = 1L;
}