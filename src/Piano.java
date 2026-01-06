import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;


public class Piano {

    int boardWidth = 800;
    int boardHeight = 600; 

    JFrame frame = new JFrame();

    //game panel
    Image backgroundPiano = new ImageIcon(getClass().getResource("/images/backgroundv2.png")).getImage();
	ImagePanel background = new ImagePanel(backgroundPiano);

    //menu panel
    Image titleImg =  new ImageIcon(getClass().getResource("/images/title screen.png")).getImage();
    ImagePanel titleScreen = new ImagePanel(titleImg); 

    JLabel teddyLabel = new JLabel(); 
    JLabel pawLabel = new JLabel();

    //keys
    JButton[] pianoKeys = new JButton[7];
    JButton[] blackKeys = new JButton[6];  
    
    //red buttons
    JButton quackButton = new JButton();
    JButton barkButton = new JButton();
    JButton bongosButton = new JButton();
    JButton drumsButton = new JButton();

    //music buttons
    JButton nebulousButton = new JButton();
    JButton overdriverButton = new JButton();
    JButton waltzButton = new JButton();
    JButton whereButton = new JButton();

    //start button
    JButton playButton = new JButton();

    //adds audio that are active to this list
    List<Clip> activeClips = new ArrayList<>();

    //all sounds used
    Map<String, Clip> soundMap = new HashMap<>();

    Piano(){
        frame.setSize(boardWidth, boardHeight);
		frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //x button closes program
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        startMenu();
    }

    void startMenu(){
        preloadSounds();

        //plays silent clip to warm up sounds
        Clip silentClip = soundMap.get("clap");
        if (silentClip != null) {
            silentClip.setFramePosition(0);
            turnDownVol(-70.0f, silentClip);
            silentClip.start();
        }

        frame.add(titleScreen, BorderLayout.CENTER);
        titleScreen.setLayout(null);

        //sets paw as cursor
        titleScreen.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            new ImageIcon(getClass().getResource("/images/teddy paw.png")).getImage(),
            new Point(0,0),"src/images/teddy paw.png")
            );

        titleScreen.add(playButton);
        playButton.setBounds(160, 435, 460, 110);
        transparentKeys(playButton);

        //if start button clicked
        playButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    frame.remove(titleScreen); //remove start panel
                    gameMenu();
                    frame.add(background, BorderLayout.CENTER); //add game panel to frame
                    frame.revalidate();
                    frame.repaint();
                }
        });
    }

    void gameMenu(){
        //frame.add(background); //adds background image
        background.setLayout(null);

        //adds white keys
        for (int i = 0; i < 7; i++){
            JButton key = new JButton();
            pianoKeys[i] = key;
            background.add(key);
            background.setComponentZOrder(key, 0); //first layer
            key.setBounds((i * 100) + 39, 134, 100, 330);

            //make keys transparent
            transparentKeys(key);
        }

        //adds black keys
        for (int i = 0; i < 6; i++){
            JButton key = new JButton();
            blackKeys[i] = key;
            background.add(key);
            background.setComponentZOrder(key, 1); //second layer
            key.setBounds((i * 100) + 110, 134, 56, 175);

            //make keys transparent
            transparentKeys(key);
        }
        blackKeys[2].setVisible(false);

        //red buttons
        quackButton.setBounds(328, 6, 45,38);
        background.add(quackButton);
        transparentKeys(quackButton);
        pressKey(quackButton, "quack", 0.0f);
        
        barkButton.setBounds(399, 6, 45,38);
        background.add(barkButton);
        transparentKeys(barkButton);
        pressKey(barkButton, "bark", 0.0f);

        bongosButton.setBounds(328, 50, 45,38);
        background.add(bongosButton);
        transparentKeys(bongosButton);
        pressKey(bongosButton, "bongos2", 0.0f);

        drumsButton.setBounds(399, 50, 45,38);
        background.add(drumsButton);
        transparentKeys(drumsButton);
        pressKey(drumsButton, "drum", 0.0f);

        //music buttons
        nebulousButton.setBounds(110, 18, 70, 61);
        background.add(nebulousButton);
        transparentKeys(nebulousButton);
        pressKey(nebulousButton, "nebulous", 0.0f);

        overdriverButton.setBounds(220, 18, 70, 61);
        background.add(overdriverButton);
        transparentKeys(overdriverButton);
        pressKey(overdriverButton, "overdriver", 0.0f);

        waltzButton.setBounds(485, 18, 70, 61);
        background.add(waltzButton);
        transparentKeys(waltzButton);
        pressKey(waltzButton, "waltz", 0.0f);

        whereButton.setBounds(608, 18, 70, 61);
        background.add(whereButton);
        transparentKeys(whereButton);
        pressKey(whereButton, "where", 0.0f);

        //teddy bear
        teddyLabel.setIcon(new ImageIcon(getClass().getResource("/images/teddy bear.png")));
        background.add(teddyLabel);
        Dimension size = teddyLabel.getPreferredSize();
        teddyLabel.setBounds(280, 405, size.width, size.height);
        background.setComponentZOrder(teddyLabel, 3);//fourth layer

        //sets paw as cursor
        background.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            new ImageIcon(getClass().getResource("/images/teddy paw.png")).getImage(),
            new Point(0,0),"src/images/teddy paw.png")
            );

        //click white keys
        pressKey(pianoKeys[0], "c", 0.0f);
        pressKey(pianoKeys[1], "d", 0.0f);
        pressKey(pianoKeys[2], "e", 0.0f);
        pressKey(pianoKeys[3], "f", 0.0f);
        pressKey(pianoKeys[4], "g", 0.0f);
        pressKey(pianoKeys[5], "a", 0.0f);
        pressKey(pianoKeys[6], "b", 0.0f);

        //click black keys
        pressKey(blackKeys[0], "c#", -20.0f);
        pressKey(blackKeys[1], "d#", -20.0f);
        pressKey(blackKeys[3], "f#", -20.0f);
        pressKey(blackKeys[4], "g#", -20.0f);
        pressKey(blackKeys[5], "a#", -20.0f);
    }


    void pressKey(JButton key, String note, float numDecVol){
        key.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopAllAudio();

                    try{
                        Clip clip = soundMap.get(note);
                        clip.setFramePosition(0); // rewind to start
                        activeClips.add(clip);
                        clip.start();

                        // Get mouse location relative to background
                        java.awt.Point p = java.awt.MouseInfo.getPointerInfo().getLocation();
                        java.awt.Point relative = new java.awt.Point(p);
                        javax.swing.SwingUtilities.convertPointFromScreen(relative, background);

                        // Center teddy on mouse X and Y
                        int newTeddyX = Math.max(0, Math.min(relative.x - teddyLabel.getWidth()/2,
                                                    background.getWidth() - teddyLabel.getWidth()));

                        pawLabel.setLocation(relative.x - 40, relative.y - 40);
                        teddyLabel.setLocation(newTeddyX, teddyLabel.getY());

                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
        });
    }


    void transparentKeys(JButton key){
            key.setContentAreaFilled(false);
            key.setBorderPainted(false);
            key.setOpaque(false);
    }

    void turnDownVol(float num, Clip clip){
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(num); // Reduce volume by num decibels.
    }


    //loads sounds from the file
    Clip loadSound(String soundFile) throws Exception {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }



    //preloads all sounds onto hash map so program runs faster
    void preloadSounds(){
        try{
            soundMap.put("c", loadSound("/sounds/piano-notes/c4.wav"));
            soundMap.put("d", loadSound("/sounds/piano-notes/d4.wav"));
            soundMap.put("e", loadSound("/sounds/piano-notes/e4.wav"));
            soundMap.put("f", loadSound("/sounds/piano-notes/f4.wav"));
            soundMap.put("g", loadSound("/sounds/piano-notes/g4.wav"));
            soundMap.put("a", loadSound("/sounds/piano-notes/a5.wav"));
            soundMap.put("b", loadSound("/sounds/piano-notes/b5.wav"));
            soundMap.put("c#", loadSound("/sounds/piano-notes/c-4.wav"));
            soundMap.put("d#", loadSound("/sounds/piano-notes/d-4.wav"));
            soundMap.put("f#", loadSound("/sounds/piano-notes/f-4.wav"));
            soundMap.put("g#", loadSound("/sounds/piano-notes/g-4.wav"));
            soundMap.put("a#", loadSound("/sounds/piano-notes/a-5.wav"));
            soundMap.put("bark", loadSound("/sounds/bark.wav"));
            soundMap.put("bongos2", loadSound("/sounds/bongos2.wav"));
            soundMap.put("quack", loadSound("/sounds/quack.wav"));
            soundMap.put("drum", loadSound("/sounds/random drum.wav"));
            soundMap.put("nebulous", loadSound("/sounds/Nebulous.wav"));
            soundMap.put("overdriver", loadSound("/sounds/The_Overdriver.wav"));
            soundMap.put("waltz", loadSound("/sounds/Waltz_of_the_Hiding_Boy.wav"));
            soundMap.put("where", loadSound("/sounds/where.wav"));
            soundMap.put("clap", loadSound("/sounds/hand_clap.wav"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    
    void stopAllAudio(){
        for (Clip clip : activeClips){
             if (clip.isRunning()) { 
                    clip.setFramePosition(0);
                    clip.stop();
                }
            }
            activeClips.clear(); // Clear the list after stopping all clips
    }


    public static void main(String[] args) {
		new Piano();

	}
}

