package ru.kislyakova.anastasia.scheduler.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kislyakova.anastasia.scheduler.entity.Channel;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Integer> {
    @Override
    List<Channel> findAll();
}
