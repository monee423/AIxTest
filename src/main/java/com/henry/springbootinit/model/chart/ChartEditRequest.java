package com.henry.springbootinit.model.chart;

import java.io.Serializable;
import lombok.Data;

/**
 * 编辑请求
 *
 */
@Data
public class ChartEditRequest implements Serializable {
    // 图表id
    private Long id;

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