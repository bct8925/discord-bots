package com.bri64.bots.chillbot.audio;

import com.bri64.bots.BotUtils;
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
  private LoopMode loopMode;
  private boolean shuffle;
  private boolean paused;
  private long pause_time;

  // Initialization
  public MusicScheduler(ChillBot main) {
    this.main = main;

    this.playlist = null;
    this.playing = false;
    this.loopMode = LoopMode.ALL;
    this.shuffle = true;

    this.paused = false;
    this.pause_time = 0;

    initAudio();
  }

  public String getTrackInfo() {
    return playlist.currentInfo();
  }

  public String getPlaylistInfo() {
    return playlist.playlistInfo();
  }

  public int getPlaylistSize() {
    return playlist.size();
  }

  public LoopMode isLoop() {
    return loopMode;
  }

  public void setLoop(LoopMode loop) {
    this.loopMode = loop;
  }

  public boolean isShuffle() {
    return shuffle;
  }

  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  public void getList(IUser user) {
    for (DankTrack track : playlist) {
      BotUtils.sendMessage(user.mention() + " " + track.toString(), user.getOrCreatePMChannel());
    }
  }

  private void initAudio() {
    playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    audioPlayer = playerManager.createPlayer();
    audioPlayer.addListener(this);
    main.getGuild().getAudioManager().setAudioProvider(new AudioProvider(audioPlayer));
    audioPlayer.setVolume(30);
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
        BotUtils.sendMessage(user.mention() + " Error loading audio for \" " + url + " \"!",
            user.getOrCreatePMChannel());
      }

      @Override
      public void loadFailed(FriendlyException throwable) {
        BotUtils.sendMessage(user.mention() + " Error loading audio for \" " + url + " \"!",
            user.getOrCreatePMChannel());
      }

      private void start() {
        if (!playing) {
          playing = true;
          if (main.getVoiceChannels().isEmpty()) {
            IVoiceChannel channel = BotUtils.getConnectedChannel(main.getGuild(), user);
            if (channel != null) {
              channel.join();
            }
          }
          playlist.goStart();
          play();
        } else if (skip) {
          playlist.goEnd();
          play();
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

  public void changeTrack(boolean next) {
    switch (loopMode) {
      case ALL: // Looping all
        if (next) {
          playlist.next();
        } else {
          playlist.prev();
        }
        play();
        break;

      case ONE: // Looping one
        play();
        break;

      default:  // Not looping
        // Last track
        if (playlist.isLast() && next) {
          stop();
        } else if (playlist.isFirst() && !next) {
          stop();
        }
        // Any other track
        else {
          if (next) {
            playlist.next();
          } else {
            playlist.prev();
          }
          play();
        }
        break;
    }
  }

  public void shuffle() {
    playlist.shuffle();
    changeTrack(true);
  }

  public void remove() {
    playlist.removeCurrent();
    play();
  }

  public boolean seek(String search) {
    return (playlist != null) && playlist.seek(search);
  }

  public void stop() {
    playlist = null;
    main.setStatus(null);
    audioPlayer.stopTrack();
    main.leaveChannels();
    playing = false;
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
      changeTrack(true);
    }
  }

  // Helpers
  void stopTrack() {
    audioPlayer.stopTrack();
  }

  private DankTrack newTrack(AudioTrack track, String url) {
    return new DankTrack(this, track, url);
  }
}
