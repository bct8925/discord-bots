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

    String pause_icon =
        scheduler.isPaused() ? "<i class=\"fas fa-play\"></i>" : "<i class=\"fas fa-pause\"></i>";
    vm.put("pause", pause_icon);

    /*String loop_icon;
    switch (scheduler.getLoop()) {
      case ALL:
        loop_icon = "<i class=\"fas fa-sync-alt\"></i>";
        break;
      case ONE:
        loop_icon = "<span class=\"fa-layers fa-fw\">"
            + "<i class=\"fas fa-sync-alt\">"
            + "</i><span class=\"fa-layers-counter\" style=\"font-size: 32px;\">1</span>"
            + "</span>";
        break;
      default:
        loop_icon = "<i class=\"fa-inverse fas fa-sync-alt\"></i>";
        break;
    }
    vm.put("loop", loop_icon);*/

    vm.put("song", scheduler.getTrackTitle());
    vm.put("playlist", scheduler.getPlaylistInfo().replaceAll("\n", "<br>"));
    return templateEngine.render(new ModelAndView(vm, "home.ftl"));
  }
}
