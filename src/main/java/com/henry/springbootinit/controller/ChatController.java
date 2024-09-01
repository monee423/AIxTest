package com.henry.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.henry.springbootinit.common.BaseResponse;
import com.henry.springbootinit.common.ErrorCode;
import com.henry.springbootinit.common.ResultUtils;
import com.henry.springbootinit.exception.ThrowUtils;
import com.henry.springbootinit.model.chart.DefectAnalysisRequest;
import com.henry.springbootinit.model.entity.Chart;
import com.henry.springbootinit.model.entity.User;
import com.henry.springbootinit.model.vo.DefectResponse;
import com.henry.springbootinit.service.ChartService;
import com.henry.springbootinit.service.UserService;
import com.henry.springbootinit.utils.ExcelUtils;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author henry
 */
@RestController
@Slf4j
public class ChatController {
    @Resource
    private UserService userService;

    @Resource
    private ChartService chartServices;

    @Value("${ark.api.key}")
    private String apiKey;

    @Value("${ark.base.url}")
    private String baseUrl;

    @Value("${ark.api.model}")
    private String modelId;

    @Value("${ark.base.chart-prompt}")
    private String chartSystemContext;

    @Value("${ark.base.analysis-prompt}")
    private String analysisSystemContext;

    @PostMapping("/defect")
    public BaseResponse<DefectResponse> chatWithAi(@RequestPart("file") MultipartFile multipartFile,
                                                   DefectAnalysisRequest defectAnalysisRequest, HttpServletRequest request) {
        String goal = defectAnalysisRequest.getGoal();
        String chartType = defectAnalysisRequest.getChartType();
        String chartName = defectAnalysisRequest.getChartName();
        String dataDesc = defectAnalysisRequest.getDataDesc();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析数据为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 15, ErrorCode.PARAMS_ERROR, "图表名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONEMB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONEMB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);
        
        String csvData = ExcelUtils.excelToCsv(multipartFile);

        StringBuilder userContext = new StringBuilder();

        userContext.append("需求描述：").append("\n选择'").append(goal).append("'进行转化\n");
        userContext.append("数据描述：").append(dataDesc).append("\n");
        userContext.append("图表类型:").append(chartType).append("\n");
        userContext.append("csv数据：").append("\n").append(csvData).append("\n");
        
        // 发送请求
        ArkService chartService = ArkService.builder().apiKey(apiKey).baseUrl(baseUrl).build();
        List<ChatMessage> chartMessages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(chartSystemContext).build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userContext.toString()).build();
        chartMessages.add(systemMessage);
        chartMessages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(modelId).messages(chartMessages).build();
        String chartResponse = chartService.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent().toString();
        chartResponse = chartResponse.replace("`", "").replace("option = ","");
        JSONObject jsonObject = new JSONObject(chartResponse);

        ArkService analysisSrvice = ArkService.builder().apiKey(apiKey).baseUrl(baseUrl).build();
        List<ChatMessage> analysisMessages = new ArrayList<>();
        systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(analysisSystemContext).build();
        userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userContext.toString()).build();
        analysisMessages.add(systemMessage);
        analysisMessages.add(userMessage);
        ChatCompletionRequest analysisCompletionRequest = ChatCompletionRequest.builder().model(modelId).messages(analysisMessages).build();
        String analysisResponse = analysisSrvice.createChatCompletion(analysisCompletionRequest).getChoices().get(0).getMessage().getContent().toString();

        // 插入到数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(String.valueOf(jsonObject));
        chart.setGenResult(analysisResponse);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartServices.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        DefectResponse defectResponse = new DefectResponse();
        defectResponse.setGenChart(String.valueOf(jsonObject));
        defectResponse.setGenResult(analysisResponse);

        return ResultUtils.success(defectResponse);
    }
}