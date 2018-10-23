package com.bri64.bots.audio.send;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Playlist implements Iterable<Track> {

  private List<Track> tracks;
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
  public void play() {
    if (current == null) {
      goStart();
    }
    current.play();
  }

  public void shuffle() {
    Collections.shuffle(tracks);
  }

  public boolean seek(String search) {
    if (current != null) {
      Track start = current;
      try {
        Track cur = tracks.get(tracks.indexOf(start) + 1);//getTrack();
        while (!cur.titleMatches(search)) {
          cur = tracks.get(tracks.indexOf(cur) + 1);
        }

        current = cur;
        current.play();
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
        current.play();
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
      current = getFirst();
    }
  }

  public void goEnd() {
    if (!tracks.isEmpty()) {
      current = getLast();
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
      result.append(tracks.get(i).toString());
      result.append("\n");
    }
    return result.toString();
  }

  public String currentText() {
    return (current != null) ? current.toString() : "null";
  }

  public String currentInfo() {
    return (current != null) ? current.info() : "null";
  }

  public boolean isFirst() {
    return current != null && current.equals(getFirst());
  }

  public boolean isLast() {
    return current != null && current.equals(getLast());
  }

  // Helpers
  private Track getFirst() {
    return (Track) ((LinkedList) tracks).getFirst();
  }

  private Track getLast() {
    return (Track) ((LinkedList) tracks).getLast();
  }

  @Override
  public Iterator<Track> iterator() {
    return tracks.iterator();
  }
}
