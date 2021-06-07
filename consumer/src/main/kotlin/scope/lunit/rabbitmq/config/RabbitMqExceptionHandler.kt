package scope.lunit.rabbitmq.config

import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer
import java.util.concurrent.RejectedExecutionException

class RabbitMqExceptionHandler : RejectAndDontRequeueRecoverer() {
    override fun recover(message: Message?, cause: Throwable?) {
        println("RabbitMqExceptionHandler :: Exception Occurred")
    }
}