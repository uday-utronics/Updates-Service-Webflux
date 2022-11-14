package com.springReactive.updatesService.controller;

import com.springReactive.updatesService.model.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaController {

//    @Autowired
//   Flux<ReceiverRecord<Integer, EmployeeRequest>> reactiveKafkaReceiver;
    String sourceTopic = "app_updates";
    String sendertopic = "emp_updates";

    String DLQtopic = "employee_DLQ";
    @Autowired
    ReceiverOptions<Integer, EmployeeRequest> receiverOptions;

    @Autowired
    KafkaSender<Integer, String> senderDLQ;
    @Autowired
    KafkaSender<Integer,EmployeeRequest> sender;

    Flux<EmployeeRequest> reactiveKafkaReceiver(ReceiverOptions<Integer, EmployeeRequest> kafkaReceiverOptions) {
        return KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(sourceTopic))).receive()
                .map(emp -> new EmployeeRequest(emp.value().getEmployeeId(),
                        emp.value().getEmployeeName(),
                        emp.value().getEmployeeCity(),
                        emp.value().getEmployeePhone(),emp.value().getJavaExperience(),emp.value().getSpringExperience()
                        ));
    }
    @EventListener(ApplicationStartedEvent.class)
    public void onMessage() {
        reactiveKafkaReceiver(receiverOptions)
                .doOnNext(r -> {

                    if(isFullOfNulls(r)){
                        SenderRecord<Integer,String, Integer> message =
                                SenderRecord.create(new ProducerRecord<>(DLQtopic,r.getEmployeeId(),r.toString()+"faled due to null value"),r.getEmployeeId());
                        senderDLQ.send(Mono.just(message)).subscribe();
                    }
                    else {
                        SenderRecord<Integer,EmployeeRequest, Integer> message =
                                SenderRecord.create(new ProducerRecord<>(sendertopic,r.getEmployeeId(),r),r.getEmployeeId());
                        sender.send(Mono.just(message)).subscribe();
                        log.info(r.toString());
                    }})
            .doOnError(e -> log.error("KafkaFlux exception", e))
            .subscribe();
    }

    boolean isFullOfNulls(EmployeeRequest o) {

        boolean a = o.getEmployeeName()==null;
        boolean b = o.getEmployeeCity()==null;
        boolean c = o.getEmployeePhone()==null;

        if(a||b||c){
            return false;
        }
        else
            return true;
    }
}
