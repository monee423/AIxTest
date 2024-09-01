package com.henry.springbootinit.model.chart;

import com.henry.springbootinit.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 * @author henry
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    // 图表名称
    private String chartName;

    // 图表id
    private Long id;

    // 图表类型
    private String chartType;

    // 用户id
    private Long userId;

    private static final long serialVersionUID = 1L;
}