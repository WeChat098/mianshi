package com.yhzhang.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhzhang.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.yhzhang.mianshiya.model.entity.QuestionBankQuestion;
import com.yhzhang.mianshiya.model.entity.User;
import com.yhzhang.mianshiya.model.vo.QuestionBankQuestionVO;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 *
 * @author <a href="https://github.com/WeChat098">程序员yhzhang</a>
 
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);



    void batchAddQuestiontoBank(List<Long> questionList, Long questionBankId, User user);

    void batchRemoveQuestionfromBank(List<Long> questionList, Long questionBankId);

    void batchAddQuestiontoBankInner(List<QuestionBankQuestion> questionBankQuestionList);
}
