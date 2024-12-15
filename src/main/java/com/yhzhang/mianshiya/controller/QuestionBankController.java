package com.yhzhang.mianshiya.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.yhzhang.mianshiya.annotation.AuthCheck;
import com.yhzhang.mianshiya.common.BaseResponse;
import com.yhzhang.mianshiya.common.DeleteRequest;
import com.yhzhang.mianshiya.common.ErrorCode;
import com.yhzhang.mianshiya.common.ResultUtils;
import com.yhzhang.mianshiya.constant.UserConstant;
import com.yhzhang.mianshiya.exception.BusinessException;
import com.yhzhang.mianshiya.exception.ThrowUtils;
import com.yhzhang.mianshiya.model.dto.question.QuestionQueryRequest;
import com.yhzhang.mianshiya.model.dto.questionBank.QuestionBankAddRequest;
import com.yhzhang.mianshiya.model.dto.questionBank.QuestionBankEditRequest;
import com.yhzhang.mianshiya.model.dto.questionBank.QuestionBankQueryRequest;
import com.yhzhang.mianshiya.model.dto.questionBank.QuestionBankUpdateRequest;
import com.yhzhang.mianshiya.model.entity.Question;
import com.yhzhang.mianshiya.model.entity.QuestionBank;
import com.yhzhang.mianshiya.model.entity.User;
import com.yhzhang.mianshiya.model.vo.QuestionBankVO;
import com.yhzhang.mianshiya.model.vo.QuestionVO;
import com.yhzhang.mianshiya.service.QuestionBankQuestionService;
import com.yhzhang.mianshiya.service.QuestionBankService;
import com.yhzhang.mianshiya.service.QuestionService;
import com.yhzhang.mianshiya.service.UserService;
import kotlin.jvm.Throws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 题库接口
 *
 * @author <a href="https://github.com/WeChat098">程序员yhzhang</a>
 
 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    // region 增删改查

    /**
     * 创建题库
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
//        ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
//        // todo 在此处将实体类和 DTO 进行转换
//        QuestionBank questionBank = new QuestionBank();
//        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
//        // 数据校验
//        questionBankService.validQuestionBank(questionBank, true);
//        // todo 填充默认值
//        User loginUser = userService.getLoginUser(request);
//        questionBank.setUserId(loginUser.getId());
//        // 写入数据库
//        boolean result = questionBankService.save(questionBank);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        // 返回新写入的数据 id
//        long newQuestionBankId = questionBank.getId();
//        return ResultUtils.success(newQuestionBankId);
        // 判断用户输入的内容是否为空
        ThrowUtils.throwIf(questionBankAddRequest == null,ErrorCode.PARAMS_ERROR);
        // DTO 和 实体类进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
        long id = userService.getLoginUser(request).getId();
        questionBank.setId(id);
        Boolean flag = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!flag,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionBank.getUserId()); // 返回成功的结果
    }

    /**
     * 删除题库
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
//        if (deleteRequest == null || deleteRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User user = userService.getLoginUser(request);
//        long id = deleteRequest.getId();
//        // 判断是否存在
//        QuestionBank oldQuestionBank = questionBankService.getById(id);
//        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可删除
//        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        // 操作数据库
//        boolean result = questionBankService.removeById(id);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        return ResultUtils.success(true);
        ThrowUtils.throwIf(deleteRequest == null,ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(deleteRequest.getId() < 0,ErrorCode.PARAMS_ERROR);
        // 验证是否存在
        Long questionBankId = deleteRequest.getId();
        boolean flag = questionBankService.getById(questionBankId) != null;
        ThrowUtils.throwIf(!flag,ErrorCode.OPERATION_ERROR);
        // 只有自己或者本人可以删除
        User user = userService.getLoginUser(request);
        if (!userService.isAdmin(request) || !Objects.equals(questionBankService.getById(questionBankId).getUserId(), user.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionBankService.removeById(questionBankId);
        return ResultUtils.success(true);

    }

    /**
     * 更新题库（仅管理员可用）
     * 首先判断输入的内容是否为空，之后将dto类转换成为实体类 判断是否存在 操作数据库 判断是否成功 返回结果
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // 更新题库信息需要管理员才能进行操作
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        // 判断是否存在
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库（封装类）
     *
     * @param
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVO(QuestionBankQueryRequest questionBankQueryRequest) {
        ThrowUtils.throwIf(questionBankQueryRequest == null,ErrorCode.PARAMS_ERROR);
        QuestionBank questionBank = questionBankService.getById(questionBankQueryRequest.getId());
        boolean isNeed = questionBankQueryRequest.isNeedQueryQuestionList();
        String key = "bank_detail_" + questionBankQueryRequest.getId();
        if (JdHotKeyStore.isHotKey(key)) {
            // 从本地缓存中读取值
            Object o = JdHotKeyStore.get(key);
            return ResultUtils.success((QuestionBankVO) o);
        }
        QuestionBankVO questionBankVO = new QuestionBankVO();
        if (isNeed) {
            QuestionQueryRequest questionQueryRequest1 = new QuestionQueryRequest();
            questionQueryRequest1.setQuestionBankId(questionBankQueryRequest.getId());
            Page<Question> page = questionService.listQuestionByPage(questionQueryRequest1);
            questionBankVO.setQuestionPage(page);
        }
        JdHotKeyStore.smartSet(key, questionBankVO);
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 分页获取题库列表（仅管理员可用）
     *
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        return ResultUtils.success(questionBankPage);
    }

    /**
     * 分页获取题库列表（封装类） 这个方法暂时没有实现对ip地址的限流访问
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
//    @PostMapping("/list/page/vo")
//    @SentinelResource(value = "listQuestionBankVOByPage"
//            , blockHandler = "blockHandler"
//            , fallback = "fallback"
//            , exceptionsToIgnore = {IllegalArgumentException.class})
//    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
//                                                               HttpServletRequest request) {
//        long current = questionBankQueryRequest.getCurrent();
//        long size = questionBankQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        // 查询数据库
//        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
//                questionBankService.getQueryWrapper(questionBankQueryRequest));
//        // 获取封装类
//        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
//    }
    @PostMapping("/list/page/vo")
    @SentinelResource(value = "listQuestionBankVOByPage"
            , blockHandler = "blockHandler"
            , fallback = "fallback"
            , exceptionsToIgnore = {IllegalArgumentException.class})
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                                       HttpServletRequest request) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        String ipAddress = request.getRemoteAddr();
        log.info("ipAddress:{}",ipAddress);
        Entry entry = null;
        try {
            entry = SphU.entry("listQuestionBankVOByPage", EntryType.IN,1,ipAddress);
            Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                    questionBankService.getQueryWrapper(questionBankQueryRequest));
            // 获取封装类
            return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));

        }catch (BlockException e) {
            if (e instanceof DegradeException) {
                return fallback(questionBankQueryRequest,request,e);
            }
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"系统压力过大，请稍后再试,发生熔断");
        } finally {
            if (entry != null) {
                entry.exit(1,ipAddress);
            }
        }
    }






    public BaseResponse<Page<QuestionBankVO>> blockHandler(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest request, BlockException ex) {
        log.error("blockHandler");
        if (ex instanceof DegradeException) {
            return fallback(questionBankQueryRequest,request,ex);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"系统压力过大，请稍后再试,发生熔断");
    }
    public BaseResponse<Page<QuestionBankVO>> fallback(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest request, Throwable ex) {
        log.error("fallback");
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"系统压力过大，请稍后再试，发生降级");
    }

    /**
     * 分页获取当前登录用户创建的题库列表
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankQueryRequest.setUserId(loginUser.getId());
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    /**
     * 编辑题库（给用户使用）
     *
     * @param questionBankEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest, HttpServletRequest request) {
        if (questionBankEditRequest == null || questionBankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankEditRequest, questionBank);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionBankEditRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
