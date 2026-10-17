package com.example.musica.client;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Decodifica el MP3 por streaming (JLayer) y lo reproduce; respeta los volúmenes Maestro y Música del juego. */
public class ReproductorAudio {
    private static volatile Hilo actual;

    public static void reproducir(Path archivo) {
        detener();
        Hilo h = new Hilo(archivo);
        actual = h;
        h.start();
    }

    public static void detener() {
        Hilo h = actual;
        actual = null;
        if (h != null) h.parar();
    }

    private static final class Hilo extends Thread {
        private final Path archivo;
        private volatile boolean parar = false;

        Hilo(Path archivo) {
            super("musica-audio");
            setDaemon(true);
            this.archivo = archivo;
        }

        void parar() {
            parar = true;
        }

        @Override
        public void run() {
            SourceDataLine linea = null;
            try (InputStream in = new BufferedInputStream(Files.newInputStream(archivo), 1 << 16)) {
                Bitstream bs = new Bitstream(in);
                Decoder dec = new Decoder();
                Header h = bs.readFrame();
                if (h == null) return;
                SampleBuffer sb = (SampleBuffer) dec.decodeFrame(h, bs);

                AudioFormat fmt = new AudioFormat(dec.getOutputFrequency(), 16, dec.getOutputChannels(), true, false);
                linea = AudioSystem.getSourceDataLine(fmt);
                linea.open(fmt);
                aplicarVolumen(linea);
                linea.start();

                int tramas = 0;
                while (h != null && !parar) {
                    short[] pcm = sb.getBuffer();
                    int len = sb.getBufferLength();
                    byte[] bytes = new byte[len * 2];
                    for (int i = 0; i < len; i++) {
                        bytes[2 * i] = (byte) (pcm[i] & 0xFF);
                        bytes[2 * i + 1] = (byte) ((pcm[i] >> 8) & 0xFF);
                    }
                    linea.write(bytes, 0, bytes.length);
                    bs.closeFrame();
                    if (++tramas % 20 == 0) aplicarVolumen(linea);
                    h = bs.readFrame();
                    if (h != null) sb = (SampleBuffer) dec.decodeFrame(h, bs);
                }
                if (!parar) linea.drain();
            } catch (Exception ex) {
                if (!parar) ClienteMusica.aviso("No se pudo reproducir el audio (¿archivo MP3 dañado?).");
            } finally {
                if (linea != null) {
                    linea.stop();
                    linea.close();
                }
            }
        }
    }

    private static void aplicarVolumen(SourceDataLine linea) {
        try {
            Options o = Minecraft.getInstance().options;
            float v = o.getSoundSourceVolume(SoundSource.MASTER) * o.getSoundSourceVolume(SoundSource.MUSIC);
            if (linea.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl c = (FloatControl) linea.getControl(FloatControl.Type.MASTER_GAIN);
                float db = v <= 0.0001f ? c.getMinimum() : (float) (20.0 * Math.log10(v));
                c.setValue(Math.max(c.getMinimum(), Math.min(c.getMaximum(), db)));
            }
        } catch (Exception ignored) {
        }
    }
}
