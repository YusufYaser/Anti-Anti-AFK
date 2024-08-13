package xyz.yusufyaser.aaafk.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.yusufyaser.aaafk.Bypass;

@Mixin(ClientPlayNetworkHandler.class)
public class OnGameMessage {

	@Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
	public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
		Bypass.checkMessage(packet.content().getString(), ci);
	}
}