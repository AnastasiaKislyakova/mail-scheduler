package ru.kislyakova.anastasia.scheduler.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kislyakova.anastasia.scheduler.entity.Email;

import java.util.List;

public interface EmailRepository extends CrudRepository<Email, Integer> {
    @Override
    List<Email> findAll();
}
