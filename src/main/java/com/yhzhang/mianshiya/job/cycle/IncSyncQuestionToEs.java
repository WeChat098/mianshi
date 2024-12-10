package com.yhzhang.mianshiya.job.cycle;

import com.yhzhang.mianshiya.esdao.QuestionEsDao;
import com.yhzhang.mianshiya.mapper.QuestionMapper;
import com.yhzhang.mianshiya.model.dto.question.QuestionEsDTO;
import com.yhzhang.mianshiya.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class IncSyncQuestionToEs {
    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsDao questionEsDao;

    @Scheduled
    public void run(){
        long FIVE_MINUTES = 5 * 60 * 1000;
        Date five = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Question> questionList = questionMapper.listQuestionWithDelete(five);
        if (questionList.isEmpty()) {
            log.info("no inc question");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("sync total " + total);
        for (int i = 0; i < total; i++) {
            int end = Math.min(i + pageSize, total);
            log.info("sync  " + end);
            questionEsDao.saveAll(questionEsDTOList.subList(i,end));
        }
        log.info("sync finish");
    }
}
