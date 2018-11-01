package com.bri64.discord.audio.receive;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.commands.CommandEvent;
import edu.cmu.sphinx.util.TimeFrame;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioInputStream;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("WeakerAccess")
public class Speaker {

  private DiscordBot bot;
  private IUser user;
  private AudioSample current;
  private SphinxTranslator translator;
  private long lastData;
  private long prevData;

  public Speaker(final DiscordBot bot, final IUser user) {
    this.bot = bot;
    this.user = user;
    this.current = new AudioSample(AudioSample.DiscordInput);
    this.translator = new SphinxTranslator();
    this.lastData = 0;
    this.prevData = 0;

    start();
  }

  private void start() {
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        if (prevData == lastData && current.getTime() > 0) {
          final AudioSample finished = current;
          new Thread(() -> process(finished)).start();
          current = new AudioSample(AudioSample.DiscordInput);
        }
        prevData = lastData;
      }
    }, 0, 500);
  }

  public void update(byte[] audio) {
    current.addData(audio);
    lastData = System.currentTimeMillis();
  }

  private synchronized void process(AudioSample sample) {
    //sample.write("test/test" + System.currentTimeMillis() + ".pcm");
    AudioInputStream stream = sample.getStream(AudioSample.SphinxOutput);
    translator.start(stream, new TimeFrame(sample.getTime()));
    while (!translator.isDone()) {
      BotUtils.waiting();
    }
    print();
    if (translator.getResult().toLowerCase().contains("chill")
        || translator.getResult().toLowerCase().contains("bought")) {
      bot.dispatch(new CommandEvent(bot.getGuild(),
          BotUtils.getConnectedChannel(bot.getGuild(), user),
          null, user, bot.getSymbol() + "chillstep"));
    }
  }

  public void print() {
    BotUtils.log(bot, user.getName() + " said: \"" + translator.getResult() + "\"");
  }
}
