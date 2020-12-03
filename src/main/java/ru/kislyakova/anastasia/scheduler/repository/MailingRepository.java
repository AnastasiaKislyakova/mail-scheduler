package ru.kislyakova.anastasia.scheduler.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;

import java.util.List;

public interface MailingRepository extends CrudRepository<Mailing, Integer> {
    @Override
    List<Mailing> findAll();
}
