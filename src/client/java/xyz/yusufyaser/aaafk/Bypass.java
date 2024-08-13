package xyz.yusufyaser.aaafk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Bypass {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static ServerConfig loadedConfig = null;

    public static void moveHead() {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) return;

        float oldYaw = player.getYaw();
        float oldPitch = player.getPitch();

        final int[] count = {0};

        Timer timer = new Timer();
        Random random = new Random();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                            oldYaw + ((random.nextFloat() * 2) - 1),
                            oldPitch + ((random.nextFloat() * 2) - 1),
                            player.isOnGround()
                    ));
                } catch (Exception e) {
                    return;
                }

                count[0]++;
                if (count[0] == 20) {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(oldYaw, oldPitch, player.isOnGround()));
                    timer.cancel();
                }
            }
        }, 0, 100);
    }

    public static boolean checkMessage(String message, CallbackInfo ci) {
        if (client.getCurrentServerEntry() == null) return false;
        if (loadedConfig == null || !loadedConfig.getServer().equals(client.getCurrentServerEntry().address)) {
            loadedConfig = ServerConfig.getInstance(client.getCurrentServerEntry().address);
        }

        if (!loadedConfig.enabled || message == null) return false;

        message = message.toLowerCase();

        boolean bypassAfk = false;
        for (String afkMessage : loadedConfig.messages) {
            afkMessage = afkMessage.toLowerCase();
            if (message.contains(afkMessage)) {
                bypassAfk = true;
                break;
            }
        }

        if (!bypassAfk) return false;

        if (loadedConfig.hideMessages) {
            ci.cancel();
        }

        moveHead();

        return true;
    }
}
