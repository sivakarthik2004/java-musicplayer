import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class MusicPlayerAWT extends Frame implements ActionListener, ItemListener {
    List songList;
    Button playButton, pauseButton, stopButton, nextButton, prevButton;
    Label nowPlayingLabel;

    File[] songs;
    int currentSongIndex = 0;
    Clip clip;
    boolean isPaused = false;
    long pausePosition = 0;

    public MusicPlayerAWT() {
        super("Java AWT Music Player 🎵");
        setLayout(new BorderLayout());

        // Get all songs from the music folder
        File musicDir = new File("music");
        songs = musicDir.listFiles((dir, name) -> name.endsWith(".wav"));

        // Song List Panel
        Panel topPanel = new Panel(new BorderLayout());
        topPanel.add(new Label("Available Songs:"), BorderLayout.NORTH);

        songList = new List(6, false);

        if (songs != null && songs.length > 0) {
            for (File f : songs) {
                songList.add(f.getName());
            }
        } else {
            songList.add("No .wav files found in /music folder");
        }

        songList.addItemListener(this);
        topPanel.add(songList, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Control Buttons
        Panel controlPanel = new Panel();
        prevButton = new Button("Previous");
        playButton = new Button("Play");
        pauseButton = new Button("Pause");
        stopButton = new Button("Stop");
        nextButton = new Button("Next");

        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(nextButton);
        add(controlPanel, BorderLayout.CENTER);

        // Now Playing Label
        nowPlayingLabel = new Label("Now Playing: None");
        add(nowPlayingLabel, BorderLayout.SOUTH);

        // Button listeners
        playButton.addActionListener(this);
        pauseButton.addActionListener(this);
        stopButton.addActionListener(this);
        nextButton.addActionListener(this);
        prevButton.addActionListener(this);

        // Window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (clip != null) clip.stop();
                System.exit(0);
            }
        });

        setSize(600, 300);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (songs == null || songs.length == 0) {
            nowPlayingLabel.setText("No songs available.");
            return;
        }

        switch (cmd) {
            case "Play":
                play(currentSongIndex);
                break;
            case "Pause":
                pause();
                break;
            case "Stop":
                stop();
                break;
            case "Next":
                if (currentSongIndex < songs.length - 1) {
                    currentSongIndex++;
                    songList.select(currentSongIndex);
                    play(currentSongIndex);
                }
                break;
            case "Previous":
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    songList.select(currentSongIndex);
                    play(currentSongIndex);
                }
                break;
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (songs == null || songs.length == 0) return;

        currentSongIndex = songList.getSelectedIndex();
        play(currentSongIndex);
    }

    public void play(int index) {
        if (songs == null || songs.length == 0) return;

        try {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(songs[index]);
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();

            isPaused = false;
            nowPlayingLabel.setText("Now Playing: " + songs[index].getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        } else if (clip != null && isPaused) {
            clip.setMicrosecondPosition(pausePosition);
            clip.start();
            isPaused = false;
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            nowPlayingLabel.setText("Now Playing: None");
        }
    }

    public static void main(String[] args) {
        new MusicPlayerAWT();
    }
}
