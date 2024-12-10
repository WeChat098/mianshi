package com.yhzhang.mianshiya.model.dto.questionBankQuestion;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yhzhang.mianshiya.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询题库题目关联请求
 *
 * @author <a href="https://github.com/WeChat098">程序员yhzhang</a>
 * 
 */
@ApiModel(description = "题库题目查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQuestionQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private Long notId; // 非id

    /**
     * 题库 id
     */
    @ApiModelProperty("题库ID")
    private Long questionBankId;

    /**
     * 题目 id
     */
    @ApiModelProperty("题目ID")
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}