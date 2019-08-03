import java.util.*;
import java.io.*;

public class Scouter implements Runnable{

    private File root;
    private SynchronizedQueue<File> directoryQueue;

    Scouter(SynchronizedQueue directoryQueue, File root){
        this.root = root;
        this.directoryQueue = directoryQueue;
        this.directoryQueue.registerProducer();
    }

    public void run() {
        FileFilter dirFilter = new FileFilter(){
            @Override
            public boolean accept(File path) {

                return path.isDirectory();
            }
        };

        File[] directories = root.listFiles(dirFilter);
        List<File> dirs = new ArrayList<File>(Arrays.asList(directories));

        for (File dir : dirs) {
            this.directoryQueue.enqueue(dir);
        }

        try {
            Thread[] dirsThreads = new Thread[dirs.size()];

            for (int i = 0; i < dirs.size(); i++) {
                dirsThreads[i] = new Thread(new Scouter(directoryQueue, dirs.get(i)));
                dirsThreads[i].start();
            }

            for (int i = 0; i < dirs.size(); i++) {
                dirsThreads[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Recursive thread run failed: " + e.getMessage());
        }
        this.directoryQueue.unregisterProducer();
    }
}