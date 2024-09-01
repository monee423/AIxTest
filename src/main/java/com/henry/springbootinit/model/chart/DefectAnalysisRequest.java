package com.henry.springbootinit.model.chart;


import com.henry.springbootinit.common.PageRequest;
import lombok.Data;
import java.io.Serializable;

/**
 * @author henry
 */
@Data
public class DefectAnalysisRequest extends PageRequest implements Serializable {
    // 分析数据
    private String goal;

    // 图表名称
    private String chartName;

    // 数据描述
    private String dataDesc;

    // 图表数据
    private String chartData;

    // 图表类型
    private String chartType;

    private static final long serialVersionUID = 1L;

}
