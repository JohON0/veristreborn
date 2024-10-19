/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils;

import im.expensive.Expensive;
import im.expensive.utils.client.IMinecraft;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;

public final class SoundUtil
implements IMinecraft {
    private static AudioInputStream stream;
    private static final List<Clip> CLIPS_LIST;

    public static void playSound(String location, double volume) {
        if (!Expensive.getInstance().getModuleManager().getClientTune().isState() || !((Boolean)Expensive.getInstance().getModuleManager().getClientTune().other.get()).booleanValue()) {
            return;
        }
        ArrayList<Clip> mutableClips = new ArrayList<Clip>(CLIPS_LIST);
        mutableClips.stream().filter(Objects::nonNull).filter(Line::isOpen).filter(clip -> !clip.isRunning()).forEach(Line::close);
        mutableClips.stream().filter(Objects::nonNull).filter(clip -> !clip.isOpen() || !clip.isRunning()).forEach(DataLine::stop);
        mutableClips.removeIf(clip -> !clip.isRunning());
        try {
            stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SoundUtil.class.getResourceAsStream("/assets/minecraft/eva/sounds/" + location)));
        } catch (Exception exception) {
            // empty catch block
        }
        assert (stream != null);
        try {
            mutableClips.add(AudioSystem.getClip());
        } catch (Exception exception) {
            System.out.println("Client:SoundUtil:" + exception.getMessage());
        }
        mutableClips.stream().filter(Objects::nonNull).filter(clip -> !clip.isOpen()).forEach(clip -> {
            try {
                clip.open(stream);
            } catch (Exception exception) {
                // empty catch block
            }
        });
        mutableClips.stream().filter(Objects::nonNull).filter(Line::isOpen).forEach(clip -> {
            FloatControl volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            int dbValue = (int)(Math.log((volume < 0.0 ? 0.0 : Math.min(volume, 1.0)) * 0.5) / Math.log(10.0) * 20.0);
            volumeControl.setValue(dbValue);
        });
        mutableClips.stream().filter(Objects::nonNull).filter(Line::isOpen).filter(clip -> !clip.isRunning()).forEach(DataLine::start);
    }

    public static void playSound(String location) {
        SoundUtil.playSound(location, 0.25);
    }

    private SoundUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        CLIPS_LIST = new ArrayList<Clip>();
    }

    public static class AudioClip {
        private final boolean loop;
        private boolean pause;
        private long currentPlayTime;
        private String soundName;
        private Clip clip;

        private AudioClip(String soundName, boolean loop) {
            this.soundName = soundName;
            this.loop = loop;
        }

        public static AudioClip build(String soundName, boolean loop) {
            return new AudioClip(soundName, loop);
        }

        public boolean isPlaying() {
            return this.clip != null && this.clip.isOpen() && this.clip.isRunning();
        }

        public void changeAudioTrack(String soundName) {
            this.soundName = soundName;
            this.stopPlayingAudio();
            this.startPlayingAudio();
        }

        public void setLoop(boolean loop) {
            if (this.clip == null) {
                return;
            }
            this.clip.loop(loop ? -1 : 0);
        }

        public boolean isLoop() {
            return this.loop && this.clip != null && this.clip.isOpen();
        }

        public void setPause(boolean pause) {
            if (this.pause != pause && this.clip != null && this.clip.isOpen() && this.clip.getMicrosecondLength() != 0L) {
                if (pause) {
                    this.currentPlayTime = this.clip.getMicrosecondPosition();
                    this.clip.stop();
                } else {
                    this.clip.setMicrosecondPosition(this.currentPlayTime);
                    this.setVolume(this.getVolume());
                    this.setLoop(this.isLoop());
                    this.clip.start();
                }
                this.pause = pause;
            }
        }

        public boolean isPaused() {
            return this.pause && this.clip != null && !this.clip.isRunning();
        }

        public void setVolume(float volume) {
            if (this.clip == null) {
                return;
            }
            double dbValue = Math.log((double)volume * 0.5) / Math.log(10.0) * 20.0;
            FloatControl control = (FloatControl)this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control.getValue() != (float)((int)dbValue)) {
                control.setValue((int)dbValue);
            }
        }

        private float getVolume() {
            FloatControl control = (FloatControl)this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            return control.getValue();
        }

        public void startPlayingAudio() {
            this.stopPlayingAudio();
            try {
                this.clip = AudioSystem.getClip();
                String resourcePath = "/assets/minecraft/eva/sounds/" + this.soundName;
                InputStream audioSrc = SoundUtil.class.getResourceAsStream(resourcePath);
                assert (audioSrc != null);
                try {
                    BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                    this.clip.open(inputStream);
                    this.setVolume(this.getVolume());
                    this.setLoop(this.isLoop());
                    this.clip.start();
                } catch (Exception exception) {
                    System.out.println(exception.getLocalizedMessage());
                }
            } catch (Exception exception) {
                System.out.println(exception.getLocalizedMessage());
            }
        }

        public void stopPlayingAudio() {
            if (this.clip == null) {
                return;
            }
            if (this.clip.isRunning()) {
                this.clip.stop();
            }
            if (this.clip.isOpen()) {
                this.clip.close();
            }
            this.clip = null;
        }

        public String getSoundName() {
            return this.soundName;
        }
    }

    public static class AudioClipPlayController {
        private final AudioClip audioClip;
        private Supplier<Boolean> playIf;
        private boolean stopIsAPause;
        private boolean started;

        private AudioClipPlayController(AudioClip audioClip, Supplier<Boolean> playIf, boolean stopIsAPause) {
            this.audioClip = audioClip;
            this.playIf = playIf;
            this.stopIsAPause = stopIsAPause;
        }

        public static AudioClipPlayController build(AudioClip audioClip, Supplier<Boolean> playIf, boolean stopIsAPause) {
            return new AudioClipPlayController(audioClip, playIf, stopIsAPause);
        }

        public void setPlayIf(Supplier<Boolean> playIf) {
            this.playIf = playIf;
        }

        public void setStopIsAPauseMode(boolean stopIsAPause) {
            this.stopIsAPause = stopIsAPause;
        }

        public void updatePlayingStatus() {
            if (this.started && this.audioClip.clip == null && this.playIf.get().booleanValue()) {
                this.started = false;
            }
            if (!this.started && this.playIf.get().booleanValue()) {
                this.audioClip.startPlayingAudio();
                this.started = true;
            }
            if (this.stopIsAPause) {
                this.audioClip.setPause(this.playIf.get() == false);
                return;
            }
            if (this.audioClip.isPlaying() != this.playIf.get().booleanValue()) {
                if (this.playIf.get().booleanValue()) {
                    this.audioClip.startPlayingAudio();
                } else {
                    this.audioClip.stopPlayingAudio();
                }
            }
        }

        public AudioClip getAudioClip() {
            return this.audioClip;
        }

        public boolean isSucessPlaying() {
            return this.audioClip.isPlaying();
        }
    }
}

