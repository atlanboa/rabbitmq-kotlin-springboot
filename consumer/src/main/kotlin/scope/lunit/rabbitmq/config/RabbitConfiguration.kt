package scope.lunit.rabbitmq.config

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfiguration {

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory) : SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setDefaultRequeueRejected(true)
        factory.setMessageConverter(queueMessageConverter())
        factory.setChannelTransacted(true)
        factory.setAdviceChain(
            RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3)
                .recoverer(RabbitMqExceptionHandler())
                .backOffOptions(3000, 3.0, 100000)
                .build()
        )

        return factory
    }

    private fun queueMessageConverter() : Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }
}