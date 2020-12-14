package ru.kislyakova.anastasia.scheduler.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kislyakova.anastasia.scheduler.entity.Channel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Integer> {
    @Override
    List<Channel> findAll();

    @Query(value = "select ch from Channel ch where ch.name = ?1")
    Channel findByName(String name);
}
