package scope.lunit.rabbitmq.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.ONE_QUEUE_NAME
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.ONE_ROUTING_KEY
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.TOPIC_EXCHANGE_NAME
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.TWO_QUEUE_NAME
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.TWO_ROUTING_KEY


@Configuration
class RabbitConfiguration {
    @Bean
    fun workerOneQueue(): Queue {
        return Queue(ONE_QUEUE_NAME, false)
    }

    @Bean
    fun workerTwoQueue(): Queue {
        return Queue(TWO_QUEUE_NAME, false)
    }

    @Bean
    fun topicExchange(): TopicExchange {
        return TopicExchange(TOPIC_EXCHANGE_NAME)
    }

    @Bean
    fun workerOneBinding(workerOneQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(workerOneQueue).to(topicExchange).with(ONE_ROUTING_KEY)
    }

    @Bean
    fun workerTwoBinding(workerTwoQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(workerTwoQueue).to(topicExchange).with(TWO_ROUTING_KEY)
    }
}