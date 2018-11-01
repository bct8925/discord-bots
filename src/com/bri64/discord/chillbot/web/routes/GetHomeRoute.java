package com.bri64.discord.chillbot.web.routes;

import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.send.MusicScheduler;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

@SuppressWarnings("RedundantThrows")
public class GetHomeRoute extends PlayerRoute {

  public GetHomeRoute(DiscordBot bot,
      MusicScheduler scheduler,
      TemplateEngine templateEngine) {
    super(bot, scheduler, templateEngine);
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> vm = new HashMap<>();
    vm.put("title", "ChillBot");
    vm.put("song", scheduler.getTrackTitle());
    vm.put("playlist", scheduler.getPlaylistInfo().replaceAll("\n", "<br>"));
    return templateEngine.render(new ModelAndView(vm, "home.ftl"));
  }
}
