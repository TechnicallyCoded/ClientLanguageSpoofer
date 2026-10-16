package fr.hyping.clientlanguagespoofer.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.level.ClientInformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    @Accessor("remoteClientInformation")
    void clientLanguageSpoofer$setRemoteInformation(ClientInformation information);
}
