package com.example.telegrambot.spotify.elements;

import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

@Data
@NoArgsConstructor
public class SimplifiedTrackFeatures {

    private Float acousticness;
    private Float danceability;
    private Float energy;
    private Float instrumentalness;
    private Float liveness;
    private Float loudness;
    private String modality;
    private Float speechiness;
    private Float tempo;
    private Float valence;

    public SimplifiedTrackFeatures(AudioFeatures audioFeatures) {
        this.acousticness = audioFeatures.getAcousticness() * 100;
        this.danceability = audioFeatures.getDanceability() * 100;
        this.energy = audioFeatures.getEnergy() * 100;
        this.instrumentalness = audioFeatures.getInstrumentalness() * 100;
        this.liveness = audioFeatures.getLiveness() * 100;
        this.loudness = -(audioFeatures.getLoudness() / 60) * 100;
        this.modality = audioFeatures.getMode().name().toLowerCase();
        this.speechiness = audioFeatures.getSpeechiness() * 100;
        this.tempo = audioFeatures.getTempo();
        this.valence = audioFeatures.getValence() * 100;
    }

    public String formatString(String format) {
        return String.format(format,
                acousticness, danceability, energy,
                instrumentalness, liveness, loudness,
                modality, speechiness, tempo, valence);
    }
}
