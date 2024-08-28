package com.henry.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.henry.springbootinit.mapper.ChartMapper;
import com.henry.springbootinit.model.entity.Chart;
import com.henry.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

/**
* @author monee
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-08-28 20:43:26
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

}




