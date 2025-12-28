package net.force2dev.fysix.sound;

import javax.sound.sampled.*;

/**
 * Sound effects manager
 * Uses javax.sound.sampled for basic beep sounds
 * NOTE: Toolkit.beep() doesn't work reliably on macOS, so we use javax.sound.sampled instead
 */
public class PlaySound {
    private static boolean soundEnabled = true;
    private static float masterVolume = 1.0f;
    private static boolean musicEnabled = true;
    private static float musicVolume = 0.3f; // Lower volume for background music (30%)
    private static Clip backgroundMusicClip = null;
    private static Thread musicThread = null;
    
    /**
     * Generate a simple beep tone using javax.sound.sampled
     */
    private static void beep(int frequency, int durationMs) {
        if (!soundEnabled) return;
        try {
            int sampleRate = 44100;
            int numSamples = durationMs * sampleRate / 1000;
            byte[] audioData = new byte[numSamples * 2];
            
            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                short sample = (short) (Math.sin(angle) * Short.MAX_VALUE * masterVolume);
                audioData[i * 2] = (byte) (sample & 0xFF);
                audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            AudioInputStream audioInputStream = new AudioInputStream(
                new java.io.ByteArrayInputStream(audioData),
                format,
                numSamples
            );
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            // Silently fail if audio doesn't work
        }
    }
    
    /**
     * Play thrust/engine sound
     */
    public static void thrust() {
        if (!soundEnabled) return;
        // Too annoying for continuous thrust - disabled
    }
    
    /**
     * Play explosion sound
     */
    public static void explosion() {
        beep(200, 150); // Low frequency, longer duration
    }
    
    /**
     * Play weapon fire sound
     */
    public static void fire() {
        beep(800, 50); // Higher frequency, short duration
    }
    
    /**
     * Play hit sound (when projectile hits something)
     */
    public static void hit() {
        beep(600, 80); // Medium frequency, medium duration
    }
    
    /**
     * Play shield hit sound
     */
    public static void shieldHit() {
        beep(700, 60); // Slightly different tone from regular hit
    }
    
    /**
     * Play respawn sound
     */
    public static void respawn() {
        beep(400, 200); // Lower frequency, longer duration
    }
    
    /**
     * Enable/disable sound
     */
    public static void setEnabled(boolean enabled) {
        soundEnabled = enabled;
    }
    
    /**
     * Set master volume (0.0 to 1.0)
     */
    public static void setMasterVolume(float volume) {
        masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Get sound enabled state
     */
    public static boolean isEnabled() {
        return soundEnabled;
    }
    
    /**
     * Get master volume
     */
    public static float getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Generate a simple retro-style background music loop
     * Creates a procedurally generated synth melody with chord progression
     */
    private static byte[] generateBackgroundMusic(int sampleRate, int durationSamples) {
        byte[] audioData = new byte[durationSamples * 2];
        
        // Simple chord progression: C - Am - F - G (retro game style)
        // Each chord plays for 2 seconds
        int samplesPerChord = sampleRate * 2; // 2 seconds per chord
        
        for (int sample = 0; sample < durationSamples; sample++) {
            int chordIndex = sample / samplesPerChord % 4;
            double time = (double) sample / sampleRate;
            
            // Base frequency for each chord
            int[] baseFreqs = {261, 220, 174, 196}; // C, A, F, G (Hz)
            int baseFreq = baseFreqs[chordIndex];
            
            // Create a simple melody with multiple harmonics
            double wave = 0.0;
            
            // Main melody note (changes with chord)
            double melodyFreq = baseFreq * (1.0 + 0.2 * Math.sin(time * 0.3)); // Slight variation
            wave += 0.4 * Math.sin(2.0 * Math.PI * melodyFreq * time);
            
            // Add harmony notes
            wave += 0.2 * Math.sin(2.0 * Math.PI * baseFreq * 1.25 * time); // Third
            wave += 0.2 * Math.sin(2.0 * Math.PI * baseFreq * 1.5 * time);  // Fifth
            wave += 0.1 * Math.sin(2.0 * Math.PI * baseFreq * 2.0 * time);  // Octave
            
            // Add a bass line
            double bassFreq = baseFreq * 0.5;
            wave += 0.3 * Math.sin(2.0 * Math.PI * bassFreq * time);
            
            // Apply a simple envelope to avoid clicks
            double envelope = 1.0;
            int samplesFromStart = sample % samplesPerChord;
            if (samplesFromStart < sampleRate / 10) {
                envelope = (double) samplesFromStart / (sampleRate / 10.0); // Fade in
            }
            
            // Limit amplitude to prevent clipping
            wave = Math.max(-1.0, Math.min(1.0, wave * envelope));
            
            short sampleValue = (short) (wave * Short.MAX_VALUE * musicVolume);
            audioData[sample * 2] = (byte) (sampleValue & 0xFF);
            audioData[sample * 2 + 1] = (byte) ((sampleValue >> 8) & 0xFF);
        }
        
        return audioData;
    }
    
    /**
     * Start playing background music (looping)
     */
    public static void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusicClip != null) return;
        
        musicThread = new Thread(() -> {
            try {
                int sampleRate = 44100;
                int durationSeconds = 8; // 8 second loop
                int numSamples = sampleRate * durationSeconds;
                
                byte[] audioData = generateBackgroundMusic(sampleRate, numSamples);
                
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                AudioInputStream audioInputStream = new AudioInputStream(
                    new java.io.ByteArrayInputStream(audioData),
                    format,
                    numSamples
                );
                
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(audioInputStream);
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusicClip.start();
            } catch (Exception e) {
                // Silently fail if music doesn't work
                backgroundMusicClip = null;
            }
        });
        musicThread.setDaemon(true); // Don't prevent JVM from exiting
        musicThread.start();
    }
    
    /**
     * Stop background music
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            try {
                backgroundMusicClip.stop();
                backgroundMusicClip.close();
            } catch (Exception e) {
                // Ignore
            }
            backgroundMusicClip = null;
        }
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
    }
    
    /**
     * Set music enabled/disabled
     */
    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else if (backgroundMusicClip == null) {
            startBackgroundMusic();
        }
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        // Restart music with new volume if it's playing
        if (backgroundMusicClip != null) {
            stopBackgroundMusic();
            startBackgroundMusic();
        }
    }
    
    /**
     * Get music enabled state
     */
    public static boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * Get music volume
     */
    public static float getMusicVolume() {
        return musicVolume;
    }
}
