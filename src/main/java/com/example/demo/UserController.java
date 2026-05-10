package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciamento de usuários.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final DistributedTransactionService txService;
    private final UserJpaDao userJpaDao;

    public UserController(
            DistributedTransactionService txService,
            UserJpaDao userJpaDao
    ) {

        this.txService = txService;
        this.userJpaDao = userJpaDao;
    }

    @PostMapping
    public void createUser(@RequestBody UserEntity user) {

        txService.save(user);
    }

    @GetMapping("/{id}")
    public UserEntity getUser(@PathVariable Long id) {

        return userJpaDao.findById(id);
    }

    @GetMapping
    public List<UserEntity> getAllUsers() {

        return userJpaDao.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userJpaDao.delete(id);
    }
}