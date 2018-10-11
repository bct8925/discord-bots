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

  private ChillBot bot;
  private AudioPlayerManager playerManager;
  private AudioPlayer audioPlayer;

  private Playlist playlist;
  private boolean playing;
  private LoopMode loopMode;
  private boolean shuffle;
  private boolean paused;
  private long pause_time;

  // Initialization
  public MusicScheduler(ChillBot bot) {
    this.bot = bot;

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

  public void setLoop(LoopMode loop) {
    this.loopMode = loop;
  }

  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  private void initAudio() {
    playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    audioPlayer = playerManager.createPlayer();
    audioPlayer.addListener(this);
    bot.getGuild().getAudioManager().setAudioProvider(new AudioProvider(audioPlayer));
    setVolume(30);
  }

  // Functions
  public void loadTracks(IUser user, String url, boolean skip) {
    playerManager.loadItem(url, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        if (playlist == null) {
          playlist = new Playlist(audioPlayer);
        }
        playlist.addTrack(newTrack(track, url));
        BotUtils.log(bot, "Added 1 track(s) to the queue.");
        start();
      }

      @Override
      public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (playlist == null) {
          playlist = new Playlist(audioPlayer);
        }
        for (AudioTrack track : audioPlaylist.getTracks()) {
          playlist.addTrack(newTrack(track, track.getInfo().uri));
        }
        if (shuffle) {
          playlist.shuffle();
        }
        BotUtils.log(bot, "Added " + audioPlaylist.getTracks().size() + " track(s) to the queue.");
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
          if (bot.getVoiceChannels().isEmpty()) {
            IVoiceChannel channel = BotUtils.getConnectedChannel(bot.getGuild(), user);
            if (channel != null) {
              channel.join();
              BotUtils.log(bot, "Connected to \"" + channel.getName() + "\".");
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
    if (playlist != null) {
      bot.setStatus(playlist.currentText());
      playlist.play();
    }
  }

  public void pause() {
    if (playing) {
      audioPlayer.setPaused(!audioPlayer.isPaused());
    }
  }

  public void changeTrack(boolean next) {
    if (playlist != null) {
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
  }

  public void shuffle() {
    if (playlist != null) {
      playlist.shuffle();
      playlist.goStart();
      play();
    }
  }

  public void remove() {
    if (playlist != null) {
      if (playlist.size() > 1) {
        playlist.removeCurrent();
        play();
      } else {
        stop();
      }
    }
  }

  public boolean seek(String search) {
    return (playlist != null) && playlist.seek(search);
  }

  public void setVolume(int percent) {
    audioPlayer.setVolume(percent);
  }

  public void stop() {
    playing = false;
    playlist = null;
    BotUtils.log(bot, "Cleared queue.");
    bot.clearStatus();
    stopTrack();
    bot.leaveChannels();
  }

  // Event listeners
  @Override
  public void onPlayerPause(AudioPlayer player) {
    paused = true;
    pause_time = audioPlayer.getPlayingTrack().getPosition();
    stopTrack();
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
    if (playing && !paused && endReason != AudioTrackEndReason.REPLACED) {
      changeTrack(true);
    }
  }

  // Helpers
  void stopTrack() {
    audioPlayer.stopTrack();
  }

  private Track newTrack(AudioTrack track, String url) {
    return new Track(this, track, url);
  }
}
