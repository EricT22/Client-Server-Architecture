package Client;

// TODO: make layout look somewhat more decent

import java.net.Socket;

import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class ClientGUI extends JFrame{
    private JTextArea field;
    // one or more buttons

    private Socket server;

    private String ipAddress = "localhost";
    private int APIPort = 8000;

    private HeartbeatThread heartbeat;

    private int sessionID;

    private final int WIDTH = 1000;
    private final int HEIGHT = 800; 

    private JPanel container;

    public ClientGUI(){
        super("Client");

        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(0, 0));
        this.setResizable(false);
        
        ConnectPanel connect = new ConnectPanel();
        LoginPanel login = new LoginPanel();
        MainPanel main = new MainPanel();
        AccountCreationPanel newAcct = new AccountCreationPanel();

        container = new JPanel(new CardLayout());
        container.add(connect, "CONNECT");
        container.add(login, "LOGIN");
        container.add(main, "MAIN");
        container.add(newAcct, "CR ACCT");
        

        this.add(container, BorderLayout.CENTER);

        this.setVisible(true);

        APIRequest.setIP(ipAddress + ":" + Integer.toString(APIPort));
    }

    private void displayImage(){
        // TODO: implement
    }

    private int connectToServer(String ip){
        // TODO: implement
        return 0;
    }

    private void startHeartbeat(String s){
        // TODO: implement
    }

    private void stopHeartbeat(){
        // TODO: implement
    }
    
    private void swapToPage(String pagename){
        CardLayout cl = (CardLayout)container.getLayout();
        cl.show(container, pagename);
    }

    private class ConnectPanel extends JPanel {
        private JLabel ipLabel;
        private JTextField ipField;
        private JButton connectButton;

        public ConnectPanel() {
            super();

            this.setBackground(Color.BLACK);
            
            prepareComponents();

            this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 200));
            
            this.add(ipLabel);
            this.add(ipField);
            this.add(connectButton);
        }

        private void prepareComponents() {
            ipLabel = new JLabel("IP Address: ");
            ipLabel.setFont(new Font("Arial", Font.PLAIN, 50));
            ipLabel.setForeground(Color.WHITE);

            ipField = new JTextField("", 15);
            ipField.setFont(new Font("Arial", Font.PLAIN, 50));
            ipField.setHorizontalAlignment(JTextField.CENTER);
            ipField.setBackground(Color.GRAY);
            ipField.setForeground(Color.RED);

            connectButton = new JButton("CONNECT");
            connectButton.setFont(new Font("Arial", Font.PLAIN, 50));
            connectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (connectToServer(ipField.getText()) == -1){
                        ipField.setText("404: Server not Found");
                    } else {
                        swapToPage("LOGIN");
                    }
                }
                
            });

        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    private class LoginPanel extends JPanel {
        private JLabel loginLabel;
        private JLabel userLabel;
        private JLabel passLabel;
        private JTextField userField;
        private JTextField passField;
        private JButton disconnectButton;
        private JButton loginButton;
        private JButton createAcctButton;

        public LoginPanel() {
            super();

            this.setBackground(Color.BLACK);
            
            prepareComponents();

            
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            
            this.add(loginLabel);
            this.add(Box.createRigidArea(new Dimension(0, 300)));
            this.add(userLabel);
            this.add(Box.createRigidArea(new Dimension(50, 0)));
            this.add(userField);
            this.add(Box.createRigidArea(new Dimension(250, 0)));
            this.add(passLabel);
            this.add(Box.createRigidArea(new Dimension(50, 0)));
            this.add(passField);
            this.add(Box.createRigidArea(new Dimension(250, 0)));

            JPanel buttons = new JPanel();
            buttons.setBackground(Color.BLACK);
            buttons.setLayout(new GridLayout(1, 3, 10, 0));
            buttons.add(disconnectButton);
            buttons.add(loginButton);
            buttons.add(createAcctButton);
            this.add(buttons);
            this.add(Box.createRigidArea(new Dimension(0, 200)));
        }

        private void prepareComponents() {
            loginLabel = new JLabel("                         LOGIN                         ");
            loginLabel.setFont(new Font("Arial", Font.PLAIN, 50));
            loginLabel.setForeground(Color.WHITE);
            
            userLabel = new JLabel("USERNAME");
            userLabel.setFont(new Font("Arial", Font.PLAIN, 35));
            userLabel.setForeground(Color.WHITE);

            userField = new JTextField("", 15);
            userField.setFont(new Font("Arial", Font.PLAIN, 25));
            userField.setHorizontalAlignment(JTextField.CENTER);
            userField.setBackground(Color.GRAY);
            userField.setForeground(Color.RED);

            passLabel = new JLabel("PASSWORD");
            passLabel.setFont(new Font("Arial", Font.PLAIN, 35));
            passLabel.setForeground(Color.WHITE);

            passField = new JTextField("", 15);
            passField.setFont(new Font("Arial", Font.PLAIN, 25));
            passField.setHorizontalAlignment(JTextField.CENTER);
            passField.setBackground(Color.GRAY);
            passField.setForeground(Color.RED);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    stopHeartbeat();
                    swapToPage("CONNECT");
                }
                
            });

            loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.PLAIN, 25));
            loginButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //TODO Eventually pass the session id that we receive when socket established in data
                    APIRequest loginRequest = APIRequest.makeRequest(RequestScheme.LOGIN,"");
                    boolean valid = false;
                    try {
                        valid = loginRequest.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (/*TODO:user and password combo correct */ valid)
                        swapToPage("MAIN");
                    // else if (loginCounter == 3){
                    //     acct recov pop up
                    // } else {
                    //     loginCounter++;
                    // }
                }
                
            });

            createAcctButton = new JButton("Create Account");
            createAcctButton.setFont(new Font("Arial", Font.PLAIN, 25));
            createAcctButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    swapToPage("CR ACCT");
                }
                
            });
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    private class MainPanel extends JPanel {
        private JTextField dataField;
        private JButton disconnectButton;
        private JButton logoutButton;
        private JButton saveButton;
        private JButton readButton;

        public MainPanel() {
            super();

            this.setBackground(Color.BLACK);
            
            prepareComponents();

            // TODO: needs layout fixing

            this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            
            this.add(dataField);
            this.add(Box.createRigidArea(new Dimension(0, 300)));

            JPanel buttons = new JPanel();
            buttons.setBackground(Color.BLACK);
            buttons.setLayout(new GridLayout(2, 3, 10, 200));
            buttons.add(saveButton);
            buttons.add(readButton);
            buttons.add(disconnectButton);
            buttons.add(logoutButton);
            this.add(buttons);
            this.add(Box.createRigidArea(new Dimension(0, 300)));
        }

        private void prepareComponents() {
            dataField = new JTextField("", 15);
            dataField.setFont(new Font("Arial", Font.PLAIN, 25));
            dataField.setHorizontalAlignment(JTextField.CENTER);
            dataField.setBackground(Color.GRAY);
            dataField.setForeground(Color.RED);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    stopHeartbeat();
                    swapToPage("CONNECT");
                }
                
            });

            logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Arial", Font.PLAIN, 25));
            logoutButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    swapToPage("LOGIN");
                }
                
            });

            saveButton = new JButton("Save");
            saveButton.setFont(new Font("Arial", Font.PLAIN, 25));
            saveButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: save 
                }
                
            });

            readButton = new JButton("Read");
            readButton.setFont(new Font("Arial", Font.PLAIN, 25));
            readButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: read
                }
                
            });
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    private class AccountCreationPanel extends JPanel {
        private JLabel createLabel;
        private JLabel emailLabel;
        private JLabel userLabel;
        private JLabel passLabel;
        private JTextField emailField;
        private JTextField userField;
        private JTextField passField;
        private JButton disconnectButton;
        private JButton createButton;

        public AccountCreationPanel() {
            super();

            this.setBackground(Color.BLACK);
            
            prepareComponents();

            
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            
            this.add(createLabel);
            this.add(Box.createRigidArea(new Dimension(0, 300)));
            this.add(emailLabel);
            this.add(emailField);
            this.add(userLabel);
            this.add(userField);
            this.add(passLabel);
            this.add(passField);
            this.add(disconnectButton);
            this.add(createButton);
        }

        private void prepareComponents() {
            createLabel = new JLabel("CREATE ACCOUNT");
            createLabel.setFont(new Font("Arial", Font.PLAIN, 50));
            createLabel.setForeground(Color.WHITE);

            emailLabel = new JLabel("EMAIL");
            emailLabel.setFont(new Font("Arial", Font.PLAIN, 35));
            emailLabel.setForeground(Color.WHITE);

            emailField = new JTextField("", 15);
            emailField.setFont(new Font("Arial", Font.PLAIN, 25));
            emailField.setHorizontalAlignment(JTextField.CENTER);
            emailField.setBackground(Color.GRAY);
            emailField.setForeground(Color.RED);
            
            userLabel = new JLabel("USERNAME");
            userLabel.setFont(new Font("Arial", Font.PLAIN, 35));
            userLabel.setForeground(Color.WHITE);

            userField = new JTextField("", 15);
            userField.setFont(new Font("Arial", Font.PLAIN, 25));
            userField.setHorizontalAlignment(JTextField.CENTER);
            userField.setBackground(Color.GRAY);
            userField.setForeground(Color.RED);

            passLabel = new JLabel("PASSWORD");
            passLabel.setFont(new Font("Arial", Font.PLAIN, 35));
            passLabel.setForeground(Color.WHITE);

            passField = new JTextField("", 15);
            passField.setFont(new Font("Arial", Font.PLAIN, 25));
            passField.setHorizontalAlignment(JTextField.CENTER);
            passField.setBackground(Color.GRAY);
            passField.setForeground(Color.RED);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    stopHeartbeat();
                    swapToPage("CONNECT");
                }
                
            });

            createButton = new JButton("Create Account");
            createButton.setFont(new Font("Arial", Font.PLAIN, 25));
            createButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: create acct
                    swapToPage("LOGIN");
                }
                
            });
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
