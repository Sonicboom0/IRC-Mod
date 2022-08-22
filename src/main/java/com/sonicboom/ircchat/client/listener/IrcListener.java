package com.sonicboom.ircchat.client.listener;

import com.sonicboom.ircchat.client.util.ChatUtil;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class IrcListener extends ListenerAdapter {
    @Override
    public void onMessage(MessageEvent event) {
        ChatUtil.formatIrcMessage(event.getUser().getNick(), event.getMessage());
    }

    @Override
    public void onJoin(JoinEvent event) {
        ChatUtil.sendClientMessage(event.getUser().getNick() + " has joined the chat");
    }

    @Override
    public void onQuit(QuitEvent event) {
        ChatUtil.sendClientMessage(event.getUser().getNick() + " has left the chat");
    }

}
