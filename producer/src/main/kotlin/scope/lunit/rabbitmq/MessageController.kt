package scope.lunit.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import scope.lunit.rabbitmq.config.RabbitMQConfigValue

@RestController
class MessageController(private val rabbitTemplate: RabbitTemplate) {

    @GetMapping("/worker/{workerId}")
    fun producerMessage(@PathVariable workerId: String, @RequestBody message: Message) : ResponseEntity<String>{
        println("${message.message} :: will send")
        rabbitTemplate.convertAndSend(RabbitMQConfigValue.TOPIC_EXCHANGE_NAME, "worker.".plus(workerId), message.message)
        return ResponseEntity.ok("message is send")
    }
}