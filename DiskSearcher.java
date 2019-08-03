import java.io.*;

public class DiskSearcher {

    static int DIRECTORY_QUEUE_CAPACITY = 50;
    static int RESULTS_QUEUE_CAPACITY = 50;

    private static SynchronizedQueue directories;
    private static SynchronizedQueue results;
    private static int numOfSearchers;
    private static int numOfCopiers;
    private static String pattern;
    private static File root;
    private static File dest;


    // java DiskSearcher <filename-pattern> <root directory> <destination directory><# of searchers> <# of copiers>

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("invalid arguments");
            return;
        }
        pattern = args[0];
        root = new File(args[1]);
        dest = new File(args[2]);
        numOfSearchers = Integer.parseInt(args[3]);
        numOfCopiers = Integer.parseInt(args[4]);

        directories = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        results = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);

        Thread[] copiers = new Thread[numOfCopiers];
        Thread[] searchers = new Thread[numOfSearchers];

        Thread scouter = new Thread(new Scouter(directories, root));

        if (root.isDirectory()){
            directories.enqueue(root);
        }else{
            System.out.println("Root path incorrect");
            return;
        }

        try {
            scouter.start();
            scouter.join();
        } catch (InterruptedException e) {
            System.out.println("Error occurred when closing scouter: " + e);
        }

        for (int i = 0; i < numOfSearchers; i++) {
            searchers[i] = new Thread(new Searcher(pattern, directories, results));
            searchers[i].start();
        }

        for (int i = 0; i < numOfCopiers; i++) {
            copiers[i] = new Thread(new Copier(dest, results));
            copiers[i].start();
        }

        try {
            for (int i = 0; i < numOfSearchers; i++) {
                searchers[i].join();
            }
            for (int i = 0; i < Math.max(numOfCopiers, numOfCopiers); i++) {
                copiers[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Error occurred when closing searcher or copier: " + e);
        }
    }
}
