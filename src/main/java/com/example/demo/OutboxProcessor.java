
package com.example.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxProcessor {

    private final OutboxEventRepository outboxRepository;
    private final UserMongoDao mongoDao;
    private final SagaCompensationService compensationService;

    public OutboxProcessor(
            OutboxEventRepository outboxRepository,
            UserMongoDao mongoDao,
            SagaCompensationService compensationService
    ) {
        this.outboxRepository = outboxRepository;
        this.mongoDao = mongoDao;
        this.compensationService = compensationService;
    }

    @Scheduled(fixedDelay = 5000)
    public void process() {

        List<OutboxEvent> events =
                outboxRepository.findByStatus(
                        OutboxEventStatus.PENDING
                );

        for (OutboxEvent event : events) {

            try {

                System.out.println(
                        "[OUTBOX] PROCESSANDO EVENTO "
                                + event.getId()
                );

                event.setStatus(
                        OutboxEventStatus.PROCESSING
                );

                outboxRepository.save(event);

                if ("erro".equalsIgnoreCase(
                        event.getUserName())) {

                    throw new RuntimeException(
                            "Falha simulada Mongo"
                    );
                }

                UserEntity user = new UserEntity();

                user.setId(event.getUserId());
                user.setName(event.getUserName());
                user.setEmail("outbox@demo.com");

                mongoDao.save(user);

                event.setStatus(
                        OutboxEventStatus.DONE
                );

                outboxRepository.save(event);

                System.out.println(
                        "[OUTBOX] EVENTO FINALIZADO"
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                event.setStatus(
                        OutboxEventStatus.ERROR
                );

                outboxRepository.save(event);

                compensationService.compensate(
                        event.getUserId()
                );
            }
        }
    }
}
