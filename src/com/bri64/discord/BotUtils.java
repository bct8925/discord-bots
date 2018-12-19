package com.bri64.discord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

  public static void log(DiscordBot main, String message) {
    System.out.println(
        new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + ": [" + main.getClass()
            .getSimpleName() + "] " + message);
  }

  public static void waiting() {
    System.out.print("");
  }

  public static void sendMessage(String message, IChannel channel) {
    if (channel == null) {
      return;
    }
    RequestBuffer.request(() -> channel.sendMessage(message));
  }

  public static void sendMessage(EmbedObject embed, IChannel channel) {
    if (channel == null) {
      return;
    }
    RequestBuffer.request(() -> channel.sendMessage(embed));
  }

  public static void deleteMessage(IMessage message) {
    if (!message.getChannel().isPrivate()) {
      message.delete();
    }
  }

  public static IChannel getChannelByName(IGuild guild, String name) {
    List<IChannel> channels = guild.getChannelsByName(name);
    if (channels.size() > 0) {
      return channels.get(0);
    }
    return null;
  }

  public static IVoiceChannel getConnectedChannel(IGuild guild, IUser user) {
    if (guild != null) {
      for (IVoiceChannel chnl : guild.getVoiceChannels()) {
        if (chnl.getConnectedUsers().contains(user)) {
          return chnl;
        }
      }
    }
    return null;
  }

  public static boolean isAlone(IUser user, IVoiceChannel channel) {
    if (channel == null) {
      return false;
    }
    List<IUser> connectedUsers = channel.getConnectedUsers();
    return connectedUsers.contains(user) && connectedUsers.size() == 1;
  }
}
