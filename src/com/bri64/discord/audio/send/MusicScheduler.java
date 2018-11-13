package com.bri64.discord.audio.send;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.commands.music.PlaylistChangedEvent;
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
import java.util.concurrent.Future;
import sx.blah.discord.handle.obj.IVoiceChannel;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class MusicScheduler extends AudioEventAdapter {

  private DiscordBot bot;
  private AudioPlayerManager playerManager;
  private AudioPlayer audioPlayer;
  private Playlist playlist;
  private boolean playing;
  private LoopMode loopMode;
  private boolean shuffle;
  private boolean paused;
  private long pause_time;

  public String getTrackTitle() {
    return (playlist != null) ? playlist.getCurrent().getTitle() : "No song playing!";
  }

  public String getTrackInfo() {
    return (playlist != null) ? playlist.getCurrent().info() : "No song playing!";
  }

  public String getPlaylistInfo() {
    return (playlist != null) ? playlist.playlistInfo() : "No playlist queued!";
  }

  public boolean isPaused() {
    return paused;
  }

  public LoopMode getLoop() {
    return loopMode;
  }

  public void setLoop(LoopMode loop) {
    this.loopMode = loop;
  }

  public void setShuffle(boolean shuffle) {
    this.shuffle = shuffle;
  }

  public void setVolume(int percent) {
    audioPlayer.setVolume(percent);
  }

  // Initialization
  public MusicScheduler(DiscordBot bot) {
    this.bot = bot;

    this.playlist = null;
    this.playing = false;
    this.loopMode = LoopMode.ALL;
    this.shuffle = true;

    this.paused = false;
    this.pause_time = 0;

    initAudio();
  }

  public void initAudio() {
    playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    audioPlayer = playerManager.createPlayer();
    audioPlayer.addListener(this);
    bot.getGuild().getAudioManager().setAudioProvider(new AudioProvider(audioPlayer));
    setVolume(30);
  }

  // Functions
  public boolean validateURL(String url) {
    final boolean[] success = {false};
    Future<Void> future = playerManager.loadItem(url, new AudioLoadResultHandler() {

      @Override
      public void trackLoaded(AudioTrack track) {
        success[0] = true;
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        success[0] = true;
      }

      @Override
      public void noMatches() {

      }

      @Override
      public void loadFailed(FriendlyException exception) {

      }
    });
    while (!future.isDone()) {
      BotUtils.waiting();
    }
    return success[0];
  }

  public void loadTracks(IVoiceChannel channel, String url, boolean skip) {
    playerManager.loadItem(url, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        if (playlist == null) {
          playlist = new Playlist();
        }
        playlist.addTrack(newTrack(track, url));
        BotUtils.log(bot, "Added 1 track(s) to the queue.");
        start();
      }

      @Override
      public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (playlist == null) {
          playlist = new Playlist();
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
      }

      @Override
      public void loadFailed(FriendlyException throwable) {
      }

      private void start() {
        if (!playing) {
          playing = true;
          if (bot.getVoiceChannels().isEmpty()) {
            if (channel != null) {
              bot.joinChannel(channel);
            }
          }
          playlist.goStart();
          play();
        } else if (skip) {
          playlist.goEnd();
          play();
        } else {
          bot.dispatch(new PlaylistChangedEvent(bot.getGuild(), null, false, false));
        }
      }
    });
  }

  private void play() {
    if (playlist != null) {
      bot.setStatus(playlist.getCurrent().getTitle());
      bot.dispatch(new PlaylistChangedEvent(bot.getGuild(), playlist.getCurrent(), false, false));
      pause_time = 0;
      if (!paused) {
        playlist.play(audioPlayer);
      }
    }
  }

  public void pause(boolean pause) {
    if (playing) {
      if (pause) {
        paused = true;
        pause_time = audioPlayer.getPlayingTrack().getPosition();
        audioPlayer.stopTrack();
        bot.dispatch(new PlaylistChangedEvent(bot.getGuild(), playlist.getCurrent(), true, false));
      } else {
        AudioTrack track = playlist.getCurrent().cloneTrack(audioPlayer);
        track.setPosition(pause_time);
        audioPlayer.playTrack(track);
        pause_time = 0;
        paused = false;
        bot.dispatch(new PlaylistChangedEvent(bot.getGuild(), playlist.getCurrent(), false, true));
      }
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
            kill();
          } else if (playlist.isFirst() && !next) {
            kill();
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
        kill();
      }
    }
  }

  public boolean seek(String search) {
    return (playlist != null) && playlist.seek(audioPlayer, search);
  }

  public void kill() {
    stop();
    bot.dispatch(new PlaylistChangedEvent(bot.getGuild(), null, false, false));
    bot.leaveChannels();
  }

  private void stop() {
    playing = false;
    playlist = null;
    BotUtils.log(bot, "Cleared queue.");
    bot.setStatus(null);
    audioPlayer.stopTrack();
  }

  public void sendSilence() {
    if (!playing) {
      playerManager.loadItem("http://brianstrains.com/silence.wav",
          new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
              audioPlayer.playTrack(track);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
              System.out.println(exception.getMessage());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
            }
          });
    }
  }

  /*// Event listeners
  @Override
  public void onPlayerPause(AudioPlayer player) {
    paused = true;
    pause_time = audioPlayer.getPlayingTrack().getPosition();
    audioPlayer.stopTrack();
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    AudioTrack track = playlist.getCurrent().cloneTrack(audioPlayer);
    track.setPosition(pause_time);
    audioPlayer.playTrack(track);
    paused = false;
  }*/

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (playing && !paused && endReason != AudioTrackEndReason.REPLACED) {
      changeTrack(true);
    }
  }

  // Helpers
  private Track newTrack(AudioTrack track, String url) {
    return new Track(track, url);
  }
}
