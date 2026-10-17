package com.example.musica.client;

import com.example.musica.MusicaMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MusicaMod.MODID, value = Dist.CLIENT)
public class EventosMusica {
    @SubscribeEvent
    public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
        ClienteMusica.detener();
    }
}
