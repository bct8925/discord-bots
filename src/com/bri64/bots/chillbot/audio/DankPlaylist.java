package com.bri64.bots.chillbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class DankPlaylist implements Iterable<DankTrack> {

  private List<DankTrack> tracks;
  private DankTrack current;
  private AudioPlayer player;

  // Initialization
  public DankPlaylist(AudioPlayer player) {
    this.tracks = new LinkedList<>();
    this.current = null;
    this.player = player;
  }

  public void addTrack(DankTrack track) {
    tracks.add(track);
  }

  // Functions
  public void play() {
    if (current == null) {
      goStart();
    }
    current.play(player);
  }

  public void shuffle() {
    Collections.shuffle(tracks);
  }

  public boolean seek(String search) {
    if (current != null) {
      DankTrack start = current;
      try {
        DankTrack cur = tracks.get(tracks.indexOf(start) + 1);//getTrack();
        while (!cur.titleMatches(search)) {
          cur = tracks.get(tracks.indexOf(cur) + 1);
        }

        current = cur;
        current.play(player);
        return true;

      } catch (IndexOutOfBoundsException ex) {
        DankTrack cur = tracks.get(0);
        if (cur.equals(start) && !cur.titleMatches(search)) {
          return false;
        }
        while (!cur.titleMatches(search)) {
          cur = tracks.get(tracks.indexOf(cur) + 1);
          if (cur.equals(start) && !cur.titleMatches(search)) {
            return false;
          }
        }

        current = cur;
        current.play(player);
        return true;
      }
    }
    return false;
  }

  // Navigation
  public void next() {
    if (current != null) {
      current = tracks.get(tracks.indexOf(current) + 1);
    }
  }

  public void goStart() {
    if (!tracks.isEmpty()) {
      current = getFirst();
    }
  }

  public void goEnd() {
    if (!tracks.isEmpty()) {
      current = getLast();
    }
  }

  // Getters
  public DankTrack getCurrent() {
    return current;
  }

  public String currentText() {
    return (current != null) ? current.toString() : "null";
  }

  public String currentInfo() {
    return (current != null) ? current.info() : "null";
  }

  public boolean isLast() {
    return current != null && current.equals(getLast());
  }

  // Helpers
  private DankTrack getFirst() {
    return (DankTrack) ((LinkedList) tracks).getFirst();
  }

  private DankTrack getLast() {
    return (DankTrack) ((LinkedList) tracks).getLast();
  }

  @Override
  public Iterator<DankTrack> iterator() {
    return tracks.iterator();
  }
}
