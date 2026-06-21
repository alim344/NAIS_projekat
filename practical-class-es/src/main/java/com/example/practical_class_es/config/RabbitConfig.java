package com.example.practical_class_es.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {


    public static final String CLASS_EVENTS_EXCHANGE = "class.events";

    public static final String ROLLBACK_QUEUE = "class.rollback.queue";
    public static final String ROLLBACK_ROUTING_KEY = "class.completed.rollback";

    public static final String CLASS_COMPLETED_QUEUE = "class.completed.queue";

    public static final String CLASS_COMPLETED_ROUTING_KEY = "class.completed";

    @Bean
    public Binding classCompletedBinding() {
        return BindingBuilder
                .bind(classCompletedQueue())
                .to(classEventsExchange())
                .with(CLASS_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public TopicExchange classEventsExchange() {
        return new TopicExchange(CLASS_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue classCompletedQueue() {
        return new Queue(CLASS_COMPLETED_QUEUE, true);
    }

    @Bean
    public Queue rollbackQueue() {
        return new Queue(ROLLBACK_QUEUE, true);
    }

    @Bean
    public Binding rollbackBinding() {
        return BindingBuilder
                .bind(rollbackQueue())
                .to(classEventsExchange())
                .with(ROLLBACK_ROUTING_KEY);
    }



    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
