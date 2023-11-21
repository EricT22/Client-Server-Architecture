package Client;

// imports being buggy, just keep them here and at the end get rid of the ones not being used

import java.net.Socket;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class ClientGUI extends JFrame{
    private JPanel clientPanel;
    private JTextArea field;
    // one or more buttons

    private Socket server;

    private String ipAddress;

    private HeartbeatThread heartbeat;

    private int sessionID;

    public ClientGUI(){

    }

    private void displayImage(){

    }

    private int connectToServer(String ip){
        return 0;
    }

    private void startHeartbeat(String s){

    }

    private void stopHeartbeat(){
        
    }
}
