package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.spotify.enums.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class TopTracksCallbackParams {

    private TimeRange timeRange;
    private Integer limit;
    private Integer offset;
    private TrackMessage trackMessage;

    public TopTracksCallbackParams(String[] parameters) {
        this.timeRange = TimeRange.getByCode(parameters[0]);
        this.limit = Integer.parseInt(parameters[1]);
        this.offset = Integer.parseInt(parameters[2]);
        if (parameters.length > 3) {
            this.trackMessage = new TrackMessage(Integer.parseInt(parameters[3]), Integer.parseInt(parameters[4]));
        }
    }

    public String[] toStringArray() {
        List<String> parametersList = new ArrayList<>();
        parametersList.add(timeRange.getCode());
        parametersList.add(limit.toString());
        parametersList.add(offset.toString());
        if (trackMessage != null) {
            parametersList.add(trackMessage.trackMessageId.toString());
            parametersList.add(trackMessage.trackMessagesSize.toString());
        }
        return parametersList.toArray(String[]::new);
    }

    public boolean hasTrackMessage() {
        return trackMessage != null;
    }

    public record TrackMessage(Integer trackMessageId, Integer trackMessagesSize){}

}
