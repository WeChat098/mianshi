package com.yhzhang.mianshiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhzhang.mianshiya.model.entity.Question;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author ZYH-wins
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-11-23 20:06:37
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("select * from question where updateTime >= #{date}")
    List<Question> listQuestionWithDelete(Date date);
}




