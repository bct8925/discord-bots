package com.bri64.discord.chillbot.web;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

import com.bri64.discord.audio.send.LoopMode;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.chillbot.ChillBot;
import com.bri64.discord.chillbot.web.routes.GetHomeRoute;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.music.LoopCommand;
import com.bri64.discord.commands.music.NextCommand;
import com.bri64.discord.commands.music.PauseCommand;
import com.bri64.discord.commands.music.PlaylistChangedEvent;
import com.bri64.discord.commands.music.PreviousCommand;
import com.bri64.discord.commands.music.ShuffleCommand;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

@SuppressWarnings("WeakerAccess")
public class WebPlayer {

  public static final String HOME_URL = "/";
  public static final String COMMAND_URL = "/command";
  public static final String REBOOT_URL = "/reboot";
  public static final String PAUSE_URL = "/pause";
  public static final String NEXT_URL = "/next";
  public static final String PREV_URL = "/prev";
  public static final String SHUFFLE_URL = "/shuffle";
  public static final String LOOP_URL = "/loop";

  private ChillBot bot;
  private MusicScheduler scheduler;
  private TemplateEngine templateEngine;
  private WebPlayerSocket socket;

  private LoopMode loop;

  public WebPlayer(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.templateEngine = new FreeMarkerEngine();

    this.loop = scheduler.getLoop();

    init();
  }

  private void init() {
    staticFileLocation("/public");
    port(8080);
    webSocket("/websocket", (socket = new WebPlayerSocket()));
    createRoutes();
  }

  private void createRoutes() {
    get(HOME_URL, new GetHomeRoute(bot, scheduler, templateEngine));

    get(COMMAND_URL, (request, response) -> {
      String command = "!" + request.queryParams("command");
      IUser bri64 = bot.getGuild().getUsersByName("bri64").get(0);
      IVoiceChannel channel = bot.getGuild().getVoiceChannelsByName("General").get(0);
      bot.dispatch(new CommandEvent(bot.getGuild(), bri64, channel, null, null, command, true));
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });

    post(LOOP_URL, (request, response) -> {
      toggleLoop();
      new LoopCommand(new CommandEvent(null, null, null, null, null,
          "!loop " + loop.name().toLowerCase(), true), scheduler).execute();
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });
    post(PAUSE_URL, (request, response) -> {
      new PauseCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });
    post(NEXT_URL, (request, response) -> {
      new NextCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });
    post(PREV_URL, (request, response) -> {
      new PreviousCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });
    post(SHUFFLE_URL, (request, response) -> {
      new ShuffleCommand(new CommandEvent(null, null, null, null, null, "", true),
          scheduler).execute();
      response.redirect(WebPlayer.HOME_URL);
      return null;
    });
  }

  @EventSubscriber
  public void onSongChange(PlaylistChangedEvent event) {
    socket.sendMessage("update");
  }

  private void toggleLoop() {
    switch (loop) {
      case ALL:
        loop = LoopMode.ONE;
        break;
      case ONE:
        loop = LoopMode.NONE;
        break;
      default:
        loop = LoopMode.ALL;
        break;
    }
  }
}
