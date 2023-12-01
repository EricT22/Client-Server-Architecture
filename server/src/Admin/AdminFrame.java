package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class AdminFrame extends JFrame{
    private Server server;

    private JTextArea queries;
    private JButton onOff;

    private AdminWorker worker;

    private final int WIDTH = 1000;
    private final int HEIGHT = 800; 

    public AdminFrame() throws Exception{
        super("Admin Console");

        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLocationRelativeTo(null);

        this.setLayout(new BorderLayout(0, 0));

        
        this.setResizable(false);

        prepareComponents();
        
        this.add(queries, BorderLayout.CENTER);
        this.add(onOff, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void prepareComponents() throws Exception {
        server = new Server();
        worker = new AdminWorker(this);

        queries = new JTextArea("", WIDTH, HEIGHT);
        queries.setBackground(Color.BLACK);
        queries.setFont(new Font("Times", Font.PLAIN, 13));
        queries.setForeground(Color.GREEN);

        onOff = new JButton("START SERVER");
        onOff.setFont(new Font("Arial", Font.PLAIN, 25));
        onOff.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (onOff.getText().equals("START SERVER")){
                    onOff.setText("STOP SERVER");

                    new Thread(server).start();
                    new Thread(worker).start();
                } else {
                    onOff.setText("START SERVER");
                    server.stop();
                    worker.stop();
                }
            }
            
        });

    }

    public static void main(String[] args) {
        try {
            new AdminFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}