package com.bri64.bots.audio.send;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DiscordBot;
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

  private DiscordBot bot;
  private AudioPlayerManager playerManager;
  private AudioPlayer audioPlayer;
  private Playlist playlist;
  private boolean playing;
  private LoopMode loopMode;
  private boolean shuffle;
  private boolean paused;
  private long pause_time;

  public String getTrackInfo() {
    return (playlist != null) ? playlist.currentInfo() : "No songs playing!";
  }

  public String getPlaylistInfo() {
    return (playlist != null) ? playlist.currentInfo() : "No playlist queued!";
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
              bot.joinChannel(channel);
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
    return (playlist != null) && playlist.seek(search);
  }

  public void kill() {
    stop();
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
    if (playing && !paused && endReason != AudioTrackEndReason.REPLACED) {
      changeTrack(true);
    }
  }

  // Helpers
  private Track newTrack(AudioTrack track, String url) {
    return new Track(audioPlayer, track, url);
  }
}
