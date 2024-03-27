package br.com.josiasmartins.picpaydesafiobackend.notification;

import br.com.josiasmartins.picpaydesafiobackend.transaction.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//import org.springframework.kafka.core.kafkaTemplate;

@Service
public class NotificationProducer {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate= kafkaTemplate;
    }

    public void sendNotification(Transaction transaction) {
        kafkaTemplate.send("notification-notification", transaction);
    }

}
