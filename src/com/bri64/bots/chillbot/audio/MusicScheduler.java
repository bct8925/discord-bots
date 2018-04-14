package com.bri64.bots.chillbot.audio;

import com.bri64.bots.DankUtils;
import com.bri64.bots.chillbot.ChillBot;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class MusicScheduler extends AudioEventAdapter {

  private ChillBot main;
  private AudioPlayerManager playerManager;
  private AudioPlayer audioPlayer;

  private DankPlaylist playlist;
  private boolean playing;
  private boolean loop;
  private boolean shuffle;
  private boolean paused;
  private long pause_time;

  // Initialization
  public MusicScheduler(ChillBot main) {
    this.main = main;

    this.playlist = null;
    this.playing = false;
    this.loop = true;
    this.shuffle = true;

    this.paused = false;
    this.pause_time = 0;

    initAudio();
  }

  public String getTrackInfo() {
    return playlist.currentInfo();
  }

  public boolean isLoop() {
    return loop;
  }

  public void setLoop(boolean loop) {
    this.loop = loop;
  }

  public boolean isShuffle() {
    return shuffle;
  }

  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  public void getList(IUser user) {
    for (DankTrack track : playlist) {
      DankUtils.sendMessage(user.mention() + " " + track.toString(), user.getOrCreatePMChannel());
    }
  }

  private void initAudio() {
    playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    audioPlayer = playerManager.createPlayer();
    audioPlayer.addListener(this);
    main.getGuild().getAudioManager().setAudioProvider(new AudioProvider(audioPlayer));
    audioPlayer.setVolume(20);
  }

  // Functions
  public void loadTracks(IUser user, String url, boolean skip) {
    playerManager.loadItem(url, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        if (playlist == null) {
          playlist = new DankPlaylist(audioPlayer);
        }
        playlist.addTrack(newTrack(track, url));
        start();
      }

      @Override
      public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (playlist == null) {
          playlist = new DankPlaylist(audioPlayer);
        }
        for (AudioTrack track : audioPlaylist.getTracks()) {
          playlist.addTrack(newTrack(track, track.getInfo().uri));
        }
        if (shuffle) {
          playlist.shuffle();
        }
        start();
      }

      @Override
      public void noMatches() {
        DankUtils.sendMessage(user.mention() + " Error loading audio for \" " + url + " \"!",
            user.getOrCreatePMChannel());
      }

      @Override
      public void loadFailed(FriendlyException throwable) {
        DankUtils.sendMessage(user.mention() + " Error loading audio for \" " + url + " \"!",
            user.getOrCreatePMChannel());
      }

      private void start() {
        if (!playing) {
          playing = true;
          if (main.getVoiceChannels().isEmpty()) {
            IVoiceChannel channel = DankUtils.getConnectedChannel(main.getGuild(), user);
            if (channel != null) {
              channel.join();
            }
          }
          playlist.play();
        } else if (skip) {
          playlist.goEnd();
          playlist.play();
        }
      }
    });
  }

  private void play() {
    main.setStatus(playlist.currentText());
    playlist.play();
  }

  public void pause() {
    audioPlayer.setPaused(!audioPlayer.isPaused());
  }

  public void skip() {
    audioPlayer.stopTrack();
  }

  public void stop() {
    playlist = null;
    main.setStatus(null);
    audioPlayer.stopTrack();
    main.leaveChannels();
    playing = false;
  }

  public boolean seek(String search) {
    return (playlist != null) && playlist.seek(search);
  }

  // Event listeners
  @Override
  public void onPlayerPause(AudioPlayer player) {
    paused = true;
    pause_time = audioPlayer.getPlayingTrack().getPosition();
    audioPlayer.stopTrack();
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    AudioTrack track = playlist.getCurrent().cloneTrack();
    track.setPosition(pause_time);
    audioPlayer.playTrack(track);
    pause_time = 0;
    paused = false;
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason != AudioTrackEndReason.REPLACED && !paused) {
      // Not looping
      if (!loop) {
        // Last track in playlist
        if (playlist.isLast()) {
          stop();
        }
        // Any other track
        else {
          playlist.next();
          play();
        }
      }
      // Looping
      else {
        // Last track
        if (playlist.isLast()) {
          playlist.goStart();
        }
        // Any other track
        else {
          playlist.next();
        }
        play();
      }
    }
  }

  // Helpers
  private DankTrack newTrack(AudioTrack track, String url) {
    return new DankTrack(this, track, url);
  }
}
