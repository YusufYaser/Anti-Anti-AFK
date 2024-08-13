package xyz.yusufyaser.aaafk.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.yusufyaser.aaafk.Bypass;

@Mixin(ClientPlayNetworkHandler.class)
public class OnTitle {
	@Inject(method = "onTitle", at = @At("HEAD"), cancellable = true)
	public void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
		Bypass.checkMessage(packet.text().getString(), ci);
	}

	@Inject(method = "onSubtitle", at = @At("HEAD"), cancellable = true)
	public void onSubtitle(SubtitleS2CPacket packet, CallbackInfo ci) {
		Bypass.checkMessage(packet.text().getString(), ci);
	}
}