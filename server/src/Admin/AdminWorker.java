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
        aFrame.queries.setText("Active Users: " + aFrame.server.getActiveUsers());
    }

    public void stop() {
        stopped = true;
    }
}
