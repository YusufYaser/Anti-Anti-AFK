package xyz.yusufyaser.aaafk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Timer;
import java.util.TimerTask;

public class AFKTimer {
    private static boolean timerRunning = false;

    private static final int CHECK_PERIOD = 5;

    private static float oldYaw = 0.0f;
    private static float oldPitch = 0.0f;
    private static int afkSeconds = 0;

    private static ServerConfig loadedConfig = null;

    public static void start() {
        if (timerRunning) return;
        timerRunning = true;

        MinecraftClient client = MinecraftClient.getInstance();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ClientPlayerEntity player = client.player;
                if (player == null) return;

                if (client.getCurrentServerEntry() == null) return;
                if (loadedConfig == null || !loadedConfig.getServer().equals(client.getCurrentServerEntry().address)) {
                    loadedConfig = ServerConfig.getInstance(client.getCurrentServerEntry().address);
                }

                if (!loadedConfig.enabled) return;
                if (loadedConfig.afkTimer == 0) return;

                if (player.getYaw() != oldYaw || player.getPitch() != oldPitch) {
                    afkSeconds = 0;
                    oldYaw = player.getYaw();
                    oldPitch = player.getPitch();
                    return;
                }

                afkSeconds += CHECK_PERIOD;
                if (afkSeconds < loadedConfig.afkTimer - 5) return;

                Bypass.moveHead();
                afkSeconds = 0;
            }
        }, 0, CHECK_PERIOD * 1000);
    }
}
