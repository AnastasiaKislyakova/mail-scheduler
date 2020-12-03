package ru.kislyakova.anastasia.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;

import java.util.List;

@RestController
@RequestMapping("api/channels")
public class ChannelController {
    private ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    Channel createChannel(@RequestBody ChannelCreationDto itemCreationDto) {
        return channelService.createChannel(itemCreationDto);
    }

    @PutMapping(value = "{channelId}")
    ResponseEntity<Channel> updateChannel(@PathVariable int channelId, @RequestBody ChannelUpdatingDto channelDto) {
        Channel channel = channelService.updateChannel(channelId, channelDto);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channel);
    }

    @GetMapping(value = "{channelId}")
    ResponseEntity<Channel> getChannelById(@PathVariable int channelId) {
        Channel channelById = channelService.getChannelById(channelId);
        if (channelById == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channelById);
    }

    @GetMapping
    List<Channel> getChannels() {
        return channelService.getChannels();
    }


}