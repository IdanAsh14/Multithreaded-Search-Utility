import java.io.*;
import java.util.regex.Pattern;

public class Searcher implements Runnable {

    private String pattern;
    private SynchronizedQueue<File> directories;
    private SynchronizedQueue<File> results;

    Searcher(String pattern, SynchronizedQueue<File> directoryQ, SynchronizedQueue<File> resultsQ){
        this.directories = directoryQ;
        this.results = resultsQ;
        this.pattern = pattern;
    }

    private boolean patternMatched(String pattern, File file){
        Pattern p = Pattern.compile(".*" + pattern + ".*");
        if (p.matcher(file.getName()).matches())
            return true;
        else
            return false;
    }

    public void run() {

        this.results.registerProducer();

        File directory;
        while ((directory = directories.dequeue()) != null) {
            FileFilter justFiles = new FileFilter() {
                @Override
                public boolean accept(File path) {
                    return path.isFile() && patternMatched(pattern, path);
                }
            };

            File [] m = directory.listFiles(justFiles);
            if (m == null)
                return;

            for (File matchedFile : m) {
                results.enqueue(matchedFile);
            }
        }
        this.results.unregisterProducer();
    }

}
