package ru.kislyakova.anastasia.scheduler.utils;

import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;

import java.util.Arrays;
import java.util.List;

public class EntityUtils {
    private static ChannelCreationDto channelCreationDto1 = new ChannelCreationDto("A", "channel A",
            Arrays.asList("ab@gmail.com", "abc@gmail.com"));

    private static ChannelCreationDto channelCreationDto2 = new ChannelCreationDto("B", "channel B",
            Arrays.asList("ab@gmail.com", "abc@gmail.com"));

    private static ChannelUpdatingDto channelUpdatingDto = new ChannelUpdatingDto("channel A",
            Arrays.asList("ab@gmail.com", "abc@gmail.com", "abcd@gmail.com"));

    public static ChannelCreationDto channelCreationDto(){
        return channelCreationDto1;
    }

    public static Channel channel() {
        return new Channel(channelCreationDto1);
    }

    public static ChannelUpdatingDto channelUpdatingDto(){
        return channelUpdatingDto;
    }

    public static List<Channel> channels() {
        Channel channel1 = new Channel(channelCreationDto1);
        channel1.setId(1);
        Channel channel2 = new Channel(channelCreationDto2);
        channel2.setId(2);
        return Arrays.asList(channel1, channel2);
    }
}
