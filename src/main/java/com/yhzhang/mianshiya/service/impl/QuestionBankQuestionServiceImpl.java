package com.yhzhang.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhzhang.mianshiya.common.ErrorCode;
import com.yhzhang.mianshiya.constant.CommonConstant;
import com.yhzhang.mianshiya.exception.BusinessException;
import com.yhzhang.mianshiya.exception.ThrowUtils;
import com.yhzhang.mianshiya.mapper.QuestionBankQuestionMapper;
import com.yhzhang.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.yhzhang.mianshiya.model.entity.Question;
import com.yhzhang.mianshiya.model.entity.QuestionBankQuestion;
import com.yhzhang.mianshiya.model.entity.User;
import com.yhzhang.mianshiya.model.vo.QuestionBankQuestionVO;
import com.yhzhang.mianshiya.model.vo.UserVO;
import com.yhzhang.mianshiya.service.QuestionBankQuestionService;
import com.yhzhang.mianshiya.service.QuestionService;
import com.yhzhang.mianshiya.service.UserService;
import com.yhzhang.mianshiya.utils.SqlUtils;
import kotlin.jvm.Throws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 *
 * @author <a href="https://github.com/WeChat098">程序员yhzhang</a>
 * 
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
        implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion 待校验的题库题目
     * @param add                  是否为新增操作 (true: 新增, false: 更新)
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // add表示当前的操作是新增记录 还是 修改记录
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        Long questionId = questionBankQuestion.getQuestionId();
        ThrowUtils.throwIf(questionId == null || questionBankId == null, ErrorCode.PARAMS_ERROR, "题库或题目不存在");
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(
            QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long userId = questionBankQuestionQueryRequest.getUserId();

        queryWrapper.eq("id", id)
                .ne(ObjectUtils.isNotEmpty(notId),"id",notId)
                .eq(ObjectUtils.isNotEmpty(questionId),"question_id", questionId)
                .eq(ObjectUtils.isNotEmpty(questionBankId),"question_bank_id", questionBankId);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion,
            HttpServletRequest request) {
        // 对象转封装类
        return QuestionBankQuestionVO.objToVo(questionBankQuestion);
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(
            Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(),
                questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream()
                .map(QuestionBankQuestionVO::objToVo).collect(Collectors.toList());

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量向题库中添加题目
     * @param questionList
     * @param questionBankId
     * @param user
     */

    @Override
    public void batchAddQuestiontoBank(List<Long> questionList, Long questionBankId, User user) {
        //前置检查
        ThrowUtils.throwIf(questionList == null || questionList.isEmpty(), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(questionBankId == null,ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(user == null,ErrorCode.PARAMS_ERROR);
        //确认每一个id都存在
        List <Question> allQuestionList = questionService.listByIds(questionList); //
        List<Long> longList = allQuestionList.stream()
                .map(Question::getId)
                .collect(Collectors.toList());//这样之后得到的都是在题库中存在id
        ThrowUtils.throwIf(longList.isEmpty(), ErrorCode.PARAMS_ERROR);

        //执行插入权限
        for (Long questionId : longList) {
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionBankId(questionBankId);
            questionBankQuestion.setQuestionId(questionId);
            questionBankQuestion.setUserId(user.getId());
            boolean result = this.save(questionBankQuestion);
            if (!result) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }


    @Override
    public void batchRemoveQuestionfromBank(List<Long> questionList, Long questionBankId) {
        int batchSize = 100;
        int totalSize = questionList.size();
        //检查
        ThrowUtils.throwIf(questionList == null || questionList.isEmpty(), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(questionBankId == null,ErrorCode.PARAMS_ERROR);
        // 执行删除关联
        QueryWrapper<QuestionBankQuestion> wrapper = new QueryWrapper<>();
        wrapper.eq("question_bank_id", questionBankId); // 找到档当前题库的所有题目
        wrapper.in("id", questionList);// 找到有的id
        for (int i = 0;i < totalSize;i += batchSize) {
//            List<QuestionBankQuestion> list = questionList.subList(i,Math.min(i + batchSize,totalSize));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestiontoBankInner(List<QuestionBankQuestion> questionBankQuestionList) {
        for (QuestionBankQuestion questionBankQuestion : questionBankQuestionList) {
            boolean result = this.save(questionBankQuestion);
            if (!result) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }

    }

}
