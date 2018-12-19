package com.bri64.discord;

import com.bri64.discord.chatbot.ChatBot;
import com.bri64.discord.chillbot.ChillBot;

public class BotLauncher {

  public static void main(String[] args) {
    new ChillBot("!", System.getenv("CHILLBOT"));
    new ChatBot("~", System.getenv("CHATBOT"));
  }
}
