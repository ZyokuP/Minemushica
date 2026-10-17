package com.example.musica;

import com.example.musica.network.ModNetwork;
import net.minecraftforge.fml.common.Mod;

@Mod(MusicaMod.MODID)
public class MusicaMod {
    public static final String MODID = "musica";

    public MusicaMod() {
        ModNetwork.registrar();
    }
}
