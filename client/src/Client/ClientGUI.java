package Client;

import java.net.InetSocketAddress;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.util.regex.Pattern;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame {
    private Socket server;

    // private String ipAddress = "";
    private int API_PORT = 8080;
    private int SOCKET_PORT = 8000;

    private HeartbeatThread heartbeat;

    private int sessionID;

    private final int WIDTH = 1000;
    private final int HEIGHT = 800;
    // TODO Implement regex checking before attempting to register
    private final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    private Pattern passPattern = Pattern.compile(PASSWORD_REGEX);

    private final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    private String username;
    private String password;

    private JPanel container;

    private int failCount = 0;

    public ClientGUI() {
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
    }

    private boolean connectToServer(String ip) throws Exception {
        if (ip.trim().equals("")) {
            return false;
        }
        APIRequest.setIP(ip + ":" + Integer.toString(API_PORT));
        try {
            System.out.println("Connecting to server " + ip + ":" + SOCKET_PORT);
            server = new Socket();
            server.connect(new InetSocketAddress(ip, SOCKET_PORT), 1000);
            sessionID = (new DataInputStream(server.getInputStream())).readInt();
            if (sessionID == -1) {
                return false;
            }
            APIRequest.setSessionID(sessionID);
        } catch (Exception e) {
            return false;
        }
        heartbeat = new HeartbeatThread(server, this);
        heartbeat.start();
        System.out.println("Connected to Server w/ Session ID" + sessionID);
        return true;
    }

    private boolean checkPasswordForm(String password) {
        return passPattern.matcher(password).matches();
    }

    private boolean checkEmailForm(String email) {
        return emailPattern.matcher(email).matches();
    }

    public void swapToPage(String pagename) {
        if (pagename.equals("LOGIN")) {
            failCount = 0;
        }
        CardLayout cl = (CardLayout) container.getLayout();
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
            ipField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    // n/a
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){
                        connectButton.doClick();
                    } else if (e.getKeyCode() == 92) {
                        JOptionPane.showMessageDialog(null, "IP: 1.1.1.1", "Solid Snake says", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // n/a
                }
                
            });

            connectButton = new JButton("CONNECT");
            connectButton.setFont(new Font("Arial", Font.PLAIN, 50));
            connectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (ipField.getText().equals("1.1.1.1")){
                            JOptionPane.showMessageDialog(null, "You thought something would happen didn't you.\nYou gullible you... :)", "Psycho Mantis?", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        if (!connectToServer(ipField.getText())) {
                            ipField.setText("404: Server not Found");
                        } else {
                            ipField.setText("");
                            swapToPage("LOGIN");
                        }
                    } catch (Exception e1) {
                    }
                }

            });

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    private class LoginPanel extends JPanel {
        private JLabel loginLabel;
        private JLabel userLabel;
        private JLabel passLabel;
        private JTextField userField;
        private JPasswordField passField;
        private JButton disconnectButton;
        private JButton loginButton;
        private JButton createAcctButton;

        public LoginPanel() {
            super();

            this.setBackground(Color.BLACK);

            prepareComponents();

            this.setLayout(new BorderLayout());

            JPanel mainLabelPanel = new JPanel();
            mainLabelPanel.setBackground(Color.BLACK);
            mainLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            mainLabelPanel.add(loginLabel);
            mainLabelPanel.add(Box.createRigidArea(new Dimension(0, 300)));
            this.add(mainLabelPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.BLACK);
            contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            contentPanel.add(userLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(58, 0)));
            contentPanel.add(userField);
            contentPanel.add(passLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(50, 0)));
            contentPanel.add(passField);
            this.add(contentPanel, BorderLayout.CENTER);

            JPanel buttons = new JPanel();
            buttons.setBackground(Color.BLACK);
            buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttons.add(disconnectButton);
            buttons.add(loginButton);
            buttons.add(createAcctButton);
            buttons.add(Box.createRigidArea(new Dimension(0, 350)));
            this.add(buttons, BorderLayout.SOUTH);

            this.add(Box.createRigidArea(new Dimension(150, 0)), BorderLayout.WEST);
            this.add(Box.createRigidArea(new Dimension(150, 0)), BorderLayout.EAST);
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

            passField = new JPasswordField("", 15);
            passField.setEchoChar((char)0);
            passField.setFont(new Font("Arial", Font.PLAIN, 25));
            passField.setHorizontalAlignment(JTextField.CENTER);
            passField.setBackground(Color.GRAY);
            passField.setForeground(Color.RED);
            passField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    // n/a
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){
                        loginButton.doClick();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // n/a
                }
                
            });

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    heartbeat.stopHeartbeat();
                    userField.setText("");
                    passField.setText("");
                    swapToPage("CONNECT");
                }

            });

            loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.PLAIN, 25));
            loginButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    APIRequest loginRequest = APIRequest.makeRequest(RequestScheme.LOGIN,
                            userField.getText() + ":" + new String(passField.getPassword()));
                    boolean valid = false;
                    try {
                        valid = loginRequest.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (valid) {
                        username = userField.getText();
                        password = (new String(passField.getPassword()));
                        userField.setText("");
                        passField.setText("");
                        swapToPage("MAIN");
                    } else if (++failCount == 3) {
                        int recoveryBtn = JOptionPane.showConfirmDialog(null, "Would you like to reset this password?",
                                "Error", JOptionPane.YES_NO_OPTION);
                        if (recoveryBtn == JOptionPane.YES_OPTION) {
                            String resetUsername = JOptionPane.showInputDialog("Please enter your username.");
                            if (resetUsername != null) {
                                try {
                                    APIRequest.makeRequest(RequestScheme.ACCT_RECOVERY, resetUsername).execute();
                                } catch (Exception e1) {
                                }
                            } else {
                                swapToPage("LOGIN");
                            }
                        } else {
                            swapToPage("LOGIN");
                        }
                    } else {
                        JOptionPane.showConfirmDialog(null, "Login Rejected", "Error",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    }
                }

            });

            createAcctButton = new JButton("Create Account");
            createAcctButton.setFont(new Font("Arial", Font.PLAIN, 25));
            createAcctButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    userField.setText("");
                    passField.setText("");
                    swapToPage("CR ACCT");
                }

            });
        }

        @Override
        public Dimension getPreferredSize() {
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

            this.setLayout(new BorderLayout());

            JPanel dataPanel = new JPanel();
            dataPanel.setBackground(Color.BLACK);
            dataPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            dataPanel.add(dataField);
            dataPanel.add(Box.createRigidArea(new Dimension(0, 300)));
            this.add(dataPanel, BorderLayout.NORTH);

            JPanel buttons = new JPanel();
            buttons.setBackground(Color.BLACK);
            buttons.setLayout(new GridLayout(2, 2, 20, 50));
            buttons.add(saveButton);
            buttons.add(readButton);
            buttons.add(disconnectButton);
            buttons.add(logoutButton);
            this.add(buttons, BorderLayout.CENTER);

            this.add(Box.createRigidArea(new Dimension(0, 300)), BorderLayout.SOUTH);
            this.add(Box.createRigidArea(new Dimension(300, 0)), BorderLayout.WEST);
            this.add(Box.createRigidArea(new Dimension(300, 0)), BorderLayout.EAST);
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
                    heartbeat.stopHeartbeat();
                    dataField.setText("");
                    swapToPage("CONNECT");
                }

            });

            logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Arial", Font.PLAIN, 25));
            logoutButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        APIRequest.makeRequest(RequestScheme.LOGOUT, username + ":" + password).execute();
                    } catch (InterruptedException | ExecutionException e1) {
                    }
                    dataField.setText("");
                    swapToPage("LOGIN");
                }

            });

            saveButton = new JButton("Save");
            saveButton.setFont(new Font("Arial", Font.PLAIN, 25));
            saveButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        APIRequest.makeRequest(RequestScheme.WRITE_DATA,
                                dataField.getText() + "||" + username + ":" + password).execute();
                    } catch (InterruptedException | ExecutionException e1) {
                        e1.printStackTrace();
                    }
                }

            });

            readButton = new JButton("Read");
            readButton.setFont(new Font("Arial", Font.PLAIN, 25));
            readButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    APIRequest readReq = APIRequest.makeRequest(RequestScheme.READ_DATA, username + ":" + password);
                    try {
                        readReq.execute();
                        String response = readReq.getResponse();
                        dataField.setText(response);
                    } catch (InterruptedException | ExecutionException e1) {
                        e1.printStackTrace();
                    }
                }

            });
        }

        @Override
        public Dimension getPreferredSize() {
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
        private JPasswordField passField;
        private JButton disconnectButton;
        private JButton createButton;

        public AccountCreationPanel() {
            super();

            this.setBackground(Color.BLACK);

            prepareComponents();

            this.setLayout(new BorderLayout());

            JPanel mainLabelPanel = new JPanel();
            mainLabelPanel.setBackground(Color.BLACK);
            mainLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            mainLabelPanel.add(createLabel);
            mainLabelPanel.add(Box.createRigidArea(new Dimension(0, 300)));
            this.add(mainLabelPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setBackground(Color.BLACK);
            contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            contentPanel.add(emailLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(152, 0)));
            contentPanel.add(emailField);
            contentPanel.add(userLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(58, 0)));
            contentPanel.add(userField);
            contentPanel.add(passLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(50, 0)));
            contentPanel.add(passField);
            this.add(contentPanel, BorderLayout.CENTER);

            JPanel buttons = new JPanel();
            buttons.setBackground(Color.BLACK);
            buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttons.add(disconnectButton);
            buttons.add(createButton);
            buttons.add(Box.createRigidArea(new Dimension(0, 200)));
            this.add(buttons, BorderLayout.SOUTH);

            this.add(Box.createRigidArea(new Dimension(150, 0)), BorderLayout.WEST);
            this.add(Box.createRigidArea(new Dimension(150, 0)), BorderLayout.EAST);
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

            passField = new JPasswordField("", 15);
            passField.setEchoChar((char)0);
            passField.setFont(new Font("Arial", Font.PLAIN, 25));
            passField.setHorizontalAlignment(JTextField.CENTER);
            passField.setBackground(Color.GRAY);
            passField.setForeground(Color.RED);
            passField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    // n/a
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){
                        createButton.doClick();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // n/a
                }
                
            });

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    heartbeat.stopHeartbeat();
                    emailField.setText("");
                    userField.setText("");
                    passField.setText("");
                    swapToPage("CONNECT");
                }

            });

            createButton = new JButton("Create Account");
            createButton.setFont(new Font("Arial", Font.PLAIN, 25));
            createButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(checkEmailForm(emailField.getText()) && checkPasswordForm(new String(passField.getPassword())))) {
                        JOptionPane.showConfirmDialog(null,
                                "Invalid Username Or Password\nPassword must be eight characters and have both and uppercase and lowercase letter, a number, and a special character",
                                "Error",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        APIRequest
                                .makeRequest(RequestScheme.REGISTER,
                                        emailField.getText() + "||" + userField.getText() + ":" + new String(passField.getPassword()))
                                .execute();
                    } catch (InterruptedException | ExecutionException e1) {
                    }
                    emailField.setText("");
                    userField.setText("");
                    passField.setText("");
                    swapToPage("LOGIN");
                }

            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    public static void main(String[] args) {
        new ClientGUI();
    }

}
