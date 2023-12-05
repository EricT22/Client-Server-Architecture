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
import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.util.regex.Pattern;

import java.util.Scanner;

public class ClientGUI extends JFrame {
    private Socket server;

    // private String ipAddress = "";
    private int API_PORT = 8080;
    private int SOCKET_PORT = 8000;

    private HeartbeatThread heartbeat;

    private int sessionID;

    private final int WIDTH = 1000;
    private final int HEIGHT = 800;

    // TODO: determine if we need special chars, if we do, uncomment the code below
    private final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])"
            + /* (?=.*[@#$%^&+=]) */ "(?=\\S+$).{8,}$";
    private Pattern passPattern = Pattern.compile(PASSWORD_REGEX);

    private final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    private JPanel container;

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

    private void displayImage() {
        // TODO: implement
    }

    private boolean connectToServer(String ip) {
        if(ip.trim().equals("")){
            return false;
        }
        APIRequest.setIP(ip + ":" + Integer.toString(API_PORT));
        try {
            System.out.println("Connecting to server " + ip + ":" + SOCKET_PORT);
            server = new Socket();
            server.connect(new InetSocketAddress(ip, SOCKET_PORT), 1000);
            sessionID = (new DataInputStream(server.getInputStream())).readInt();
            APIRequest.setSessionID(sessionID);
        } catch (Exception e) {
            return false;
        }
        heartbeat = new HeartbeatThread(server);
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

    private void swapToPage(String pagename) {
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

            connectButton = new JButton("CONNECT");
            connectButton.setFont(new Font("Arial", Font.PLAIN, 50));
            connectButton.addActionListener(new ActionListener() {
                // TODO connect to server socket here
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!connectToServer(ipField.getText())) {
                        ipField.setText("404: Server not Found");
                    } else {
                        swapToPage("LOGIN");
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
        private JTextField passField;
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

            passField = new JTextField("", 15);
            passField.setFont(new Font("Arial", Font.PLAIN, 25));
            passField.setHorizontalAlignment(JTextField.CENTER);
            passField.setBackground(Color.GRAY);
            passField.setForeground(Color.RED);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.PLAIN, 25));
            disconnectButton.addActionListener(new ActionListener() {
                // TODO End Socket Connetion here. Stop heartbeat.
                @Override
                public void actionPerformed(ActionEvent e) {
                    heartbeat.stopHeartbeat();
                    swapToPage("CONNECT");
                }

            });

            loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.PLAIN, 25));
            loginButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Eventually pass the session id that we receive when socket established
                    // in data
                    APIRequest loginRequest = APIRequest.makeRequest(RequestScheme.LOGIN,
                            userField.getText() + ":" + passField.getText());
                    boolean valid = false;
                    try {
                        valid = loginRequest.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (/* TODO:user and password combo correct */ valid)
                        swapToPage("MAIN");
                    // else if (loginCounter == 3){
                    // acct recov pop up
                    // } else {
                    // loginCounter++;
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

            // TODO: needs layout fixing

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
        private JTextField passField;
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
                    heartbeat.stopHeartbeat();
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
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }
    }

    public static void main(String[] args) {
        new ClientGUI();

        /*
         * REGEX CHECK BELOW
         * if requirements change, uncomment code below. and combine first two lines.
         */

        // ClientGUI gui =

        // Scanner kb = new Scanner(System.in);
        // String search = "";
        // do {
        // System.out.print("Enter your email: ");
        // search = kb.next();

        // System.out.println((!gui.checkEmailForm(search) ? "Not a" : "A") + " valid
        // email address");
        // } while (!gui.checkEmailForm(search));

        // do {
        // System.out.println("Enter a password:" );
        // search = kb.next();
        // System.out.println((!gui.checkPasswordForm(search) ? "Not a" : "A") + " valid
        // password");
        // } while (!gui.checkPasswordForm(search));

        // kb.close();
    }

}
