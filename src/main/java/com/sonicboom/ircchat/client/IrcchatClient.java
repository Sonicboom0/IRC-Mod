package com.sonicboom.ircchat.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import java.net.Socket;
import java.util.List;


@Environment(EnvType.CLIENT)
public class IrcchatClient implements ClientModInitializer {

    public static PircBotX bot;
    public static Configuration configuration;

    private static KeyBinding keyBinding;
    public static boolean ircActivated = false;
    public static boolean chatActivated = false;

    @Override
    public void onInitializeClient() {
        System.out.println("Group chat has started !");

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ircchat.irc",
                InputUtil.Type.KEYSYM, //
                GLFW.GLFW_KEY_R, //
                "category.ircchat.irc" //
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                ircActivated = !ircActivated;
                Text ircModeText = Text.literal("Irc mode is now: ")
                        .append(Text.literal(ircActivated ? "on" : "off").formatted(ircActivated ? Formatting.GREEN : Formatting.RED));
                client.player.sendMessage(ircModeText, false);
            }
        });

        HudRenderCallback.EVENT.register((m, t) -> {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            Text ircModeText = Text.literal("Irc mode: ")
                    .append(Text.literal(ircActivated ? "on" : "off").formatted(ircActivated ? Formatting.GREEN : Formatting.RED));
            if (MinecraftClient.getInstance().inGameHud.getChatHud().getChatScreen() != null)
                renderer.draw(m, ircModeText, 4, MinecraftClient.getInstance().currentScreen.height - 25, 0xffffff);
        });

    }
}
