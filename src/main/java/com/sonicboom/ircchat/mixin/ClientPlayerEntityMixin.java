package com.sonicboom.ircchat.mixin;

import com.mojang.authlib.GameProfile;
import com.sonicboom.ircchat.client.IrcchatClient;
import com.sonicboom.ircchat.client.listener.IrcListener;
import com.sonicboom.ircchat.client.util.ChatUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void initializeIRCconnection(CallbackInfo ci){
        if (IrcchatClient.configuration == null)
            IrcchatClient.configuration = new Configuration.Builder()
                    .setName(ChatUtil.getUsername())
                    .addServer("---")
                    .addAutoJoinChannel("---")
                    .setServerPassword("---")
                    .addListener(new IrcListener())
                    .buildConfiguration();
        if (IrcchatClient.bot == null)
            IrcchatClient.bot = new PircBotX(IrcchatClient.configuration);
        if (!IrcchatClient.bot.isConnected())
            new Thread(() -> {
                try {
                    IrcchatClient.bot.startBot();
                } catch (IOException | IrcException e) {
                    e.printStackTrace();
                }
            }).start();
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", cancellable = true)
    public void onChatMessage(String message, Text preview, CallbackInfo ci) {
        if (IrcchatClient.ircActivated) {
            ci.cancel();
            if (IrcchatClient.bot != null && IrcchatClient.bot.isConnected()) {
                ChatUtil.formatIrcMessage(ChatUtil.getUsername(), message);
                IrcchatClient.bot.sendIRC().message("#minecraft", message);
            }
            else {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Not connected").formatted(Formatting.RED));
            }
        }
    }
}
