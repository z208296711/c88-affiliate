package com.c88.affiliate.config;

import com.c88.common.core.constant.TopicConstants;
import com.c88.member.dto.MemberRegisterDTO;
import com.c88.payment.vo.WithdrawVO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static com.c88.common.core.constant.TopicConstants.*;

// @EnableKafka
@Configuration
public class KafkaConsumerConfig {


//    @Bean
//    public NewTopic topic1() {
//        return new NewTopic(REMIT, 3, (short) 3);
//    }

    // @Value("${spring.kafka.bootstrap-servers}")
    // private String bootstrapAddress;
    //
    // public ConsumerFactory<String, MemberRegisterDTO> consumerFactory(String groupId) {
    //     Map<String, Object> props = new HashMap<>();
    //     props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    //     props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    //     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    //     props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
    //     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //     return new DefaultKafkaConsumerFactory<>(props,
    //             new StringDeserializer(),
    //             new JsonDeserializer<>(MemberRegisterDTO.class));
    // }
    //
    // public ConcurrentKafkaListenerContainerFactory<String, MemberRegisterDTO> kafkaListenerContainerFactory(String groupId) {
    //     ConcurrentKafkaListenerContainerFactory<String, MemberRegisterDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(consumerFactory(groupId));
    //     factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    //     return factory;
    // }
    //
    // @Bean
    // public ConcurrentKafkaListenerContainerFactory<String, MemberRegisterDTO> affiliateMemberRegisterKafkaListenerContainerFactory() {
    //     return kafkaListenerContainerFactory(MEMBER_REGISTER);
    // }

    // @Bean
    // public ConcurrentKafkaListenerContainerFactory<String, WithdrawVO> affiliateMemberWithdrawKafkaListenerContainerFactory() {
    //     Map<String, Object> props = new HashMap<>();
    //     props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    //     props.put(ConsumerConfig.GROUP_ID_CONFIG, WITHDRAW);
    //     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    //     props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
    //     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    //
    //     ConcurrentKafkaListenerContainerFactory<String, WithdrawVO> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props,
    //             new StringDeserializer(),
    //             new JsonDeserializer<>(WithdrawVO.class))
    //     );
    //
    //     factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    //
    //     return factory;
    // }


    @Bean
    public NewTopic newTopic(){
        return new NewTopic(RECHARGE_BONUS,3,(short) 3);
    }


    @Bean
    public NewTopic newTopicAffLog(){
        return new NewTopic(AFFILIATE_OPERATION_LOG,3,(short) 3);
    }


    @Bean
    public NewTopic newSaveBetOrderEvent(){
        return new NewTopic(TopicConstants.SAVE_BET_ORDER_EVENT,3,(short) 3);
    }


}
