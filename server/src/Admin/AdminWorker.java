package Admin;

public class AdminWorker implements Runnable{
    private AdminFrame aFrame;
    private boolean stopped;
    
    public AdminWorker(AdminFrame aFrame){
        this.aFrame = aFrame;
    }

    @Override
    public void run(){
        stopped = false;

        while(!stopped){
            try {
                updateData();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateData(){
        String queries = "";

        // num connected users
        queries += "Active Users: " + aFrame.server.getActiveUsers() + '\n';
        
        // num logged in users
        queries += "";

        // which users are logged in (names)
        queries += "";

        // which users are locked out (names)
        queries += "";

        // num registered users
        queries += "";

        aFrame.queries.setText(queries);
    }

    public void stop() {
        stopped = true;
    }
}
