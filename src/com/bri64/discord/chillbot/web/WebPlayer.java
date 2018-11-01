package com.bri64.discord.chillbot.web;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.chillbot.ChillBot;
import com.bri64.discord.chillbot.web.routes.GetHomeRoute;
import com.bri64.discord.chillbot.web.routes.PostNextRoute;
import com.bri64.discord.chillbot.web.routes.PostPauseRoute;
import com.bri64.discord.chillbot.web.routes.PostPrevRoute;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;

@SuppressWarnings("WeakerAccess")
public class WebPlayer {

  public static final String HOME_URL = "/";
  public static final String REBOOT_URL = "/reboot";
  public static final String PLAY_URL = "/play";
  public static final String PAUSE_URL = "/pause";
  public static final String NEXT_URL = "/next";
  public static final String PREV_URL = "/prev";

  private ChillBot bot;
  private MusicScheduler scheduler;
  private TemplateEngine templateEngine;

  public WebPlayer(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.templateEngine = new FreeMarkerEngine();
    init();
  }

  private void init() {
    staticFileLocation("/public");
    port(8080);
    createRoutes();
  }

  private void createRoutes() {
    get(HOME_URL, new GetHomeRoute(bot, scheduler, templateEngine));
    //post(REBOOT_URL, new PostRebootRoute(bot, scheduler, templateEngine));

    //post(PLAY_URL, new PostPlayRoute(bot, templateEngine));
    post(PAUSE_URL, new PostPauseRoute(bot, scheduler, templateEngine));
    post(NEXT_URL, new PostNextRoute(bot, scheduler, templateEngine));
    post(PREV_URL, new PostPrevRoute(bot, scheduler, templateEngine));
  }
}
