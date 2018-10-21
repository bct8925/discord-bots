package com.bri64.bots.audio.receive;

import com.bri64.bots.DiscordBot;
import java.util.HashMap;
import java.util.Map;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.audio.IAudioReceiver;
import sx.blah.discord.handle.obj.IUser;

public class ActivateListener implements IAudioReceiver {

  private DiscordBot bot;
  private IAudioManager manager;
  private Map<IUser, Speaker> people;


  public ActivateListener(final DiscordBot bot) {
    this.bot = bot;
    this.manager = bot.getGuild().getAudioManager();
    this.people = new HashMap<>();
    manager.subscribeReceiver(this);
  }

  @Override
  public void receive(byte[] audio, IUser user, char sequence, int timestamp) {
    if (!user.isBot()) {
      Speaker speaker = people.get(user);
      if (speaker == null) {
        speaker = new Speaker(bot, user);
        people.put(user, speaker);
      }
      speaker.update(audio);
    }
  }

  @Override
  public AudioEncodingType getAudioEncodingType() {
    return AudioEncodingType.PCM;
  }
}
