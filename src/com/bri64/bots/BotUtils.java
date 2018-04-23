package com.bri64.bots;

import java.text.SimpleDateFormat;
import java.util.Date;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.RequestBuffer;

@SuppressWarnings("WeakerAccess")
public class BotUtils {

  public static void log(Bot main, String message) {
    System.out.println(
        new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + ": [" + main.getClass()
            .getSimpleName() + "]" + message);
  }

  public static void waiting() {
    System.out.print("");
  }

  public static void sendMessage(String message, IChannel channel) {
    RequestBuffer.request(() -> channel.sendMessage(message));
  }

  public static void sendMessage(EmbedObject embed, IChannel channel) {
    RequestBuffer.request(() -> channel.sendMessage(embed));
  }

  public static void deleteMessage(IMessage message) {
    if (!message.getChannel().isPrivate()) {
      message.delete();
    }
  }

  public static IChannel getChannelByName(IGuild guild, String name) {
    for (IChannel channel : guild.getChannelsByName(name)) {
      return channel;
    }
    return null;
  }

  public static IVoiceChannel getConnectedChannel(IGuild guild, IUser user) {
    IVoiceChannel channel = null;
    for (IVoiceChannel chnl : guild.getVoiceChannels()) {
      if (chnl.getConnectedUsers().contains(user)) {
        channel = chnl;
        break;
      }
    }
    return channel;
  }
}
