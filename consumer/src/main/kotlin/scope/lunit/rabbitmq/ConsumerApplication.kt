package scope.lunit.rabbitmq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConsumerApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ConsumerApplication>(*args)
        }
    }
}