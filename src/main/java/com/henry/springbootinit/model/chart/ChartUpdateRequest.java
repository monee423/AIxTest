package com.henry.springbootinit.model.chart;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 编辑请求
 *
 */
@Data
public class ChartUpdateRequest implements Serializable {
    // 图表id
    private Long id;

    // 分析目标
    private String goal;

    // 图表数据
    private String chartData;

    // 图表类型
    private String chartType;

    // 生成图表
    private String genChart;

    // 生成结论
    private String genResult;

    // 用户id
    private Long userId;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

    // 是否删除
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}