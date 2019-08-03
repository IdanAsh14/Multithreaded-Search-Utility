import java.io.*;

public class Copier implements Runnable {
    public static final int COPY_BUFFER_SIZE = 4096;

    private File destination;
    private SynchronizedQueue<File> resultsQueue;

    Copier(File destination, SynchronizedQueue<File> resultsQueue) {
        this.destination = destination;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        try {
            if (!destination.exists()) destination.mkdirs();
            String dest = destination.getPath();

            File current;
            while ((current = resultsQueue.dequeue()) != null) {
                InputStream in = new FileInputStream(current.getPath());
                OutputStream out = new FileOutputStream(dest + File.separator + current.getName());

                byte[] buffer = new byte[COPY_BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) > 0)
                    out.write(buffer, 0, len);

                in.close();
                out.close();
            }

        } catch (IOException e) {
            System.out.println("Copier error: " + e.getMessage());
        }
    }
}