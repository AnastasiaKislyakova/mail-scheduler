package ru.kislyakova.anastasia.scheduler.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;

import java.util.List;

public interface MailingRepository extends CrudRepository<Mailing, Integer> {
    @Override
    List<Mailing> findAll();
    @Query(value = "select m from Mailing m where m.channelId = ?1 and m.text = ?2")
    List<Mailing> findByChannelIdAndText(int channelId, String text);
}
