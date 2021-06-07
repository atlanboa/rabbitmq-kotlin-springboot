[Rabbit MQ 기초 사용법 - Yun Blog | 기술 블로그](https://cheese10yun.github.io/spring-rabbitmq/)

[cheese10yun/blog-sample](https://github.com/cheese10yun/blog-sample/tree/master/rabbitmq-sample)

[AbstractMessageListenerContainer (Spring AMQP 2.3.7 API)](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/rabbit/listener/AbstractMessageListenerContainer.html#setAdviceChain-org.aopalliance.aop.Advice...-)

[RetryInterceptorBuilder (Spring AMQP 2.3.7 API)](https://docs.spring.io/spring-amqp/docs/current/api/org/springframework/amqp/rabbit/config/RetryInterceptorBuilder.html)

### consumer-side failover

[All about of RabbitMQ Consumer-Side Failover](https://medium.com/@trinhthethanh25390/all-about-of-rabbitmq-consumer-side-failover-a6672a1a78d1)

### publisher-side fail

[Types of Publishing Failures - RabbitMq Publishing Part 1 - Jack Vanlightly](https://jack-vanlightly.com/blog/2017/3/10/rabbitmq-the-different-failures-on-basicpublish)

### Stateful, Stateless retry interceptor

# Retry Interceptor

두 가지 상태의 Retry Interceptor 가 존재한다.

1. stateless
2. stateful

두 가지 상태가 존재하는 이유는 Queue 에 들어가는 task 의 처리 방식에 대한 선택이 필요하기 때문이다.

각 상태에 대해서 설명하면 아래와 같다.

- **Stateless** 의 경우 ***매번*** ***RabbitMQ 로 돌아가지 않고*** retry 를 시도하는 방식, 다 실패하면 핸들러를 통해서 로그나 에러 처리
- **Stateful** 의 경우 ***매번 RabbitMQ 로 메시지를 전송하여*** 다시 retry 하는 방식, interceptor 가 해당하는 메시지를 트래킹할 수 있는 unique key 로 시도 횟수를 카운트함.

# Publisher (Producer) : RabbitTemplate

- Publisher 가 메시지를 생성하기 위해서는 아래 코드처럼 사용합니다.

```groovy
@RestController
class MessageController(private val rabbitTemplate: RabbitTemplate) {

    @GetMapping("/worker/{workerId}")
    fun producerMessage(@PathVariable workerId: String, @RequestBody message: Message) : ResponseEntity<String>{
        println("${message.message} :: will send")
        rabbitTemplate.convertAndSend(RabbitMQConfigValue.TOPIC_EXCHANGE_NAME, "worker.".plus(workerId), message.message)
        return ResponseEntity.ok("message is send")
    }
}
```

- 위 코드와 같이 rabbitTemplate 을 DI 받아서 convertAndSend 메소드를 사용하여 exchange 에 던져주면, 메시지가 적절한 큐로 전송이 됩니다. 그렇기 때문에, 메시지를 생성하는 RabbitTemplate 의 설정을 변경하고자 한다면 아래처럼 **amqpTemplate 빈**을 직접 등록해주면 됩니다.

```groovy
@Bean
public RabbitTemplate amqpTemplate() {
  RabbitTemplate rabbitTemplate = new RabbitTemplate();
  rabbitTemplate.setConnectionFactory(connectionFactory);
  rabbitTemplate.setMandatory(true);
  rabbitTemplate.setChannelTransacted(true);
  rabbitTemplate.setReplyTimeout(60000);
  rabbitTemplate.setMessageConverter(queueMessageConverter());
  return rabbitTemplate;
}
```

설정 메소드에 대한 참조는 아래 레퍼런스를 참조하면 좋습니다.

[RabbitTemplate (Spring AMQP 2.3.1 API)](https://docs.spring.io/spring-amqp/docs/latest_ga/api/org/springframework/amqp/rabbit/core/RabbitTemplate.html)

- **setConnectionFactory**

  : RabbitMQ Connection 을 얻어올 Factory 을 지정합니다. 기본적으로 RabbitMQ dependency 를 적용하면 기본 빈이 생성되는데, 기본 빈을 주입받아 지정해줘도 무방합니다.

- **setMandatory**

  : 메시지를 전송할 때 mandatory flag 를 지정합니다 : returnCallBack 이 제공될 때만 적용됩니다.

    - What is Mandatory flag
- **setChannelTransacted**

  : 채널에 트랜잭션을 적용하여 생성되도록 하는 플래그입니다.

- **setReplyTimeout**

  : sendAndReceive 메소드 중 하나를 사용했을때 메시지의 응답을 기다리는 milliseconds 를 지정합니다.

- **setMessageConverter**

  : 사용하고자 하는 MessageConverter 를 지정합니다. 대표적으로 Jackson 이나, gson 같은 컨버터가 있겠죠?

- 이 외에 여러 설정들을 적용할 수 있습니다. 모든 것을 다 알 필요는 없으나 인지하는 것이 좋을듯 합니다.

# Consumer : SimpleRabbitListenerContainerFactory

```groovy
@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
  final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
  factory.setConnectionFactory(connectionFactory);
  factory.setDefaultRequeueRejected(false);
  factory.setMessageConverter(queueMessageConverter());
  factory.setChannelTransacted(true);
  factory.setAdviceChain(RetryInterceptorBuilder
      .stateless()
      .maxAttempts(MAX_TRY_COUNT)
      .recoverer(new RabbitMqExceptionHandler())
      .backOffOptions(INITIAL_INTERVAL, MULTIPLIER, MAX_INTERVAL)
      .build());
  return factory;
}
```

메시지를 소모하는 Consumer 입니다. 리스너를 생성하여 메시지를 처리하므로 리스너를 생성하는 **SimpleRabbitListenerContainerFactory** 빈을 설정하여 등록해주면 됩니다.

- **setConnectionFactory**

  : rabbitmq connection 을 얻어올 connectionFactory 입니다.

- **setDefaultRequeueRejected**

  : 메시지를 처리하는 과정에서 서버에서 에러를 발생시키면 큐에 다시 쌓이게 할 것인지 정합니다. default 값은 false 이고, true 로 변경하면 서버측에서 처리하지 못하는 경우 미친듯한 에러 로그가 발생하므로, 에러 핸들러를 꼭 달아주도록 합시다.

- **setMessageConverter, setChannelTransacted**

  : publisher 와 설명이 동일합니다.

- **setAdviceChain**

  : 해당 메소드로 RetryInterceptor 를 등록하여 적절한 retry 방식과 재요청 횟수 exceptionahanlder 등을 등록할 수 있게 됩니다.