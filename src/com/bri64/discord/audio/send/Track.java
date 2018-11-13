package com.bri64.discord.audio.send;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

@SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
public class Track {

  private AudioTrack track;
  private String url;

  public Track(final AudioTrack track, String url) {
    this.track = track;
    this.url = url;
  }

  public AudioTrack cloneTrack(AudioPlayer player) {
    AudioTrack clone = track.makeClone();
    if (clone.isSeekable()) {
      long startTime = 0;
      long endTime = 0;

      String stime = url.replaceAll("^.*t=([0-9.]+).*$", "$1");
      try {
        startTime = (long) (Double.parseDouble(stime) * 1000L);
      } catch (NumberFormatException ignored) {
      }

      String etime = url.replaceAll("^.*e=([0-9.]+).*$", "$1");
      try {
        endTime = (long) (Double.parseDouble(etime) * 1000L);
      } catch (NumberFormatException ignored) {
      }

      clone.setPosition(startTime);
      if (endTime != 0) {
        clone.setMarker(new TrackMarker(endTime, new StopMarker(player)));
      }
    }
    return clone;
  }

  public void play(AudioPlayer player) {
    player.playTrack(cloneTrack(player));
  }

  public boolean titleMatches(String query) {
    return track.getInfo().title.toLowerCase().matches(".*" + query.toLowerCase() + ".*");
  }

  public String info() {
    return (track == null) ? "null"
        : "\"" + getTitle() + "\", by " + track.getInfo().author + "\n" + getURL();
  }

  public String getURL() {
    return url;
  }

  public String getTitle() {
    return (track == null) ? "null" : (track.getInfo().title);
  }
}
