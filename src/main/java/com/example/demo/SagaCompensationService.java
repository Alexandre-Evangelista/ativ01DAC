
package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaCompensationService {

    private final UserJpaDao userJpaDao;

    public SagaCompensationService(UserJpaDao userJpaDao) {
        this.userJpaDao = userJpaDao;
    }

    @Transactional
    public void compensate(Long userId) {

        System.out.println(
                "[SAGA] Executando compensação"
        );

        userJpaDao.delete(userId);

        System.out.println(
                "[SAGA] Usuário removido do H2"
        );
    }
}
