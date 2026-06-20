package com.example.practical_class_es.service;

import com.example.practical_class_es.config.RabbitConfig;
import com.example.practical_class_es.doc.CandidateInfo;
import com.example.practical_class_es.doc.PracticalClassLog;
import com.example.practical_class_es.dto.CompletedDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCompletedListener {

    @Autowired
    private PracticalClassLogService logService;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @RabbitListener(queues = RabbitConfig.CLASS_COMPLETED_QUEUE)
    public void handleClassCompleted(CompletedDTO event) {
        System.out.println("PRIMIO PORUKU ALO...-----------------------------------------------------*");
        try {
            PracticalClassLog log = new PracticalClassLog();
            log.setPracticalClassId(event.getPracticalClassId());
            log.setCompleted(true);
            log.setKmDriven(event.getKmDriven());
            log.setScore(event.getScore());
            log.setStartTime(event.getStartTime());
            log.setEndTime(event.getEndTime());
            log.setInstructorNote(event.getInstructorNote());

            CandidateInfo ci = new CandidateInfo();
            ci.setId(event.getCandidateId());
            log.setCandidateInfo(ci);

            logService.create(log);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.CLASS_EVENTS_EXCHANGE,
                    "class.completed.confirmed",
                    event
            );

        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.CLASS_EVENTS_EXCHANGE,
                    "class.completed.rollback",
                    event
            );
        }
    }
}
