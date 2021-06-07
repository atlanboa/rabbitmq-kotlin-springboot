package scope.lunit.rabbitmq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.ONE_QUEUE_NAME
import scope.lunit.rabbitmq.config.RabbitMQConfigValue.Companion.TWO_QUEUE_NAME

@Component
class Listener {

    @RabbitListener(queues = [ONE_QUEUE_NAME])
    fun workerOneMessage(message: String) {
        println("before worker one IllegalArgumentException throw")
        throw IllegalArgumentException()
        println("workerOne : ${message}" )

    }

    @RabbitListener(queues = [TWO_QUEUE_NAME])
    fun workerTwoMessage(message: String) {
        println("workerTwo : ${message}" )
    }
}