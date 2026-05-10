
package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class DistributedTransactionService {

    private final UserXaDao userXaDao;
    private final OutboxEventRepository outboxRepository;
    private final XATransactionCoordinator coordinator;

    public DistributedTransactionService(
            UserXaDao userXaDao,
            OutboxEventRepository outboxRepository,
            XATransactionCoordinator coordinator
    ) {

        this.userXaDao = userXaDao;
        this.outboxRepository = outboxRepository;
        this.coordinator = coordinator;
    }

    public void save(UserEntity user) {

        try {

            coordinator.begin();

            userXaDao.save(user);

            System.out.println(
                    "[APP] Usuário salvo no H2 XA"
            );

            coordinator.delistAll();

            boolean ok = coordinator.prepare();

            if (ok) {

                coordinator.commit();

                System.out.println(
                        "[APP] COMMIT GLOBAL"
                );

            } else {

                coordinator.rollback();

                System.out.println(
                        "[APP] ROLLBACK GLOBAL"
                );

                return;
            }

            OutboxEvent event =
                    new OutboxEvent(
                            user.getId(),
                            user.getName()
                    );

            outboxRepository.save(event);

            System.out.println(
                    "[OUTBOX] Evento criado"
            );

        } catch (Exception ex) {

            System.out.println(
                    "[APP] ERRO -> rollback global"
            );

            ex.printStackTrace();

            try {

                coordinator.rollback();

            } catch (Exception ignored) {
            }

            throw new RuntimeException(ex);
        }
    }
}
