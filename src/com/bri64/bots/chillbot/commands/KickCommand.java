package com.bri64.bots.chillbot.commands;

import com.bri64.bots.chillbot.ChillBot;
import java.util.List;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class KickCommand implements Command {

  private UserVoiceChannelMoveEvent event;
  private ChillBot bot;

  public KickCommand(final UserVoiceChannelMoveEvent event, final ChillBot bot) {
    this.event = event;
    this.bot = bot;
  }

  @Override
  public void execute() {
    //System.out.println(event.getNewChannel().getRoleOverrides());
    List<ICategory> catas = bot.getGuild().getCategoriesByName("Kick");
    if (catas.size() != 0) {
      event.getNewChannel().delete();
      IVoiceChannel newChannel = bot.getGuild().createVoiceChannel("~~~ /kick ~~~");
      newChannel.changeCategory(catas.get(0));
      //IRole everyone = bot.getGuild().getEveryoneRole();
      //newChannel.overrideRolePermissions(everyone, null, EnumSet.of(Permissions.READ_MESSAGES, Permissions.VOICE_CONNECT));
    }

  }
}
