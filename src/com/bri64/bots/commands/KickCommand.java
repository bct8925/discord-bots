package com.bri64.bots.commands;

import com.bri64.bots.DiscordBot;
import java.util.List;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class KickCommand implements Command {

  private UserVoiceChannelJoinEvent event;
  private DiscordBot bot;

  public KickCommand(final UserVoiceChannelJoinEvent event, final DiscordBot bot) {
    this.event = event;
    this.bot = bot;
  }

  @Override
  public void execute() {
    //System.out.println(event.getNewChannel().getRoleOverrides());
    List<ICategory> catas = bot.getGuild().getCategoriesByName("Kick");
    if (catas.size() != 0) {
      event.getVoiceChannel().delete();
      IVoiceChannel newChannel = bot.getGuild().createVoiceChannel("~~~ /kick ~~~");
      newChannel.changeCategory(catas.get(0));
      //IRole everyone = bot.getGuild().getEveryoneRole();
      //newChannel.overrideRolePermissions(everyone, null, EnumSet.of(Permissions.READ_MESSAGES, Permissions.VOICE_CONNECT));
    }

  }
}