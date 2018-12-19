package com.bri64.discord.audio.send;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.util.Collections;
import java.util.LinkedList;

@SuppressWarnings("WeakerAccess")
public class Playlist {

  private LinkedList<Track> tracks;
  private Track current;

  public int size() {
    return tracks.size();
  }

  // Initialization
  public Playlist() {
    this.tracks = new LinkedList<>();
    this.current = null;
  }

  public void addTrack(Track track) {
    tracks.add(track);
  }

  // Functions
  public void play(AudioPlayer player) {
    if (current == null) {
      goStart();
    }
    current.play(player);
  }

  public void shuffle() {
    Collections.shuffle(tracks);
  }

  public boolean seek(AudioPlayer player, String search) {
    if (current != null) {
      Track start = current;
      try {
        Track cur = tracks.get(tracks.indexOf(start) + 1);//getTrack();
        while (!cur.titleMatches(search)) {
          cur = tracks.get(tracks.indexOf(cur) + 1);
        }

        current = cur;
        current.play(player);
        return true;

      } catch (IndexOutOfBoundsException ex) {
        Track cur = tracks.get(0);
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
      int next = (tracks.indexOf(current) + 1) % tracks.size();
      current = tracks.get(next);
    }
  }

  public void prev() {
    if (current != null) {
      int prev = (tracks.indexOf(current) - 1) % tracks.size();
      current = tracks.get((prev >= 0) ? prev : tracks.size() + prev);
    }
  }

  public void goStart() {
    if (!tracks.isEmpty()) {
      current = tracks.getFirst();
    }
  }

  public void goEnd() {
    if (!tracks.isEmpty()) {
      current = tracks.getLast();
    }
  }

  public void removeCurrent() {
    if (size() > 1) {
      Track old = current;
      next();
      tracks.remove(old);
    }
  }

  // Getters
  public Track getCurrent() {
    return current;
  }

  public String playlistInfo() {
    StringBuilder result = new StringBuilder();
    result.append("Current Queue (");
    result.append(tracks.indexOf(current) + 1);
    result.append("/");
    result.append(tracks.size());
    result.append("):\n");

    int start = tracks.indexOf(current);
    int end = start + ((tracks.size() - start >= 5) ? 5 : tracks.size() - start);
    for (int i = start; i < end; i++) {
      result.append(i + 1);
      result.append(". ");
      result.append(tracks.get(i).getTitle());
      result.append("\n");
    }
    return result.toString();
  }

  public String[] getNextSongs() {
    int start = (!isLast()) ? tracks.indexOf(current) + 1 : 0;
    return getNextSongs(start);
  }

  public String[] getNextSongs(int start) {
    int end = start + ((tracks.size() - start >= 5) ? 5 : tracks.size() - start);
    int size = end - start;
    String[] result = new String[]{"", "", "", "", ""};
    for (int i = 0; i < size; i++) {
      result[i] = (start + i + 1) + ". " + tracks.get(i + start).getTitle();
    }
    return result;
  }

  public boolean isFirst() {
    return current != null && current.equals(tracks.getFirst());
  }

  public boolean isLast() {
    return current != null && current.equals(tracks.getLast());
  }

}
