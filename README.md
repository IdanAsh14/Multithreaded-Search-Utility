# Multithreaded-Search-Utility
The utility will allow searching for file names that contain some given pattern, under some given root directory. File names that contain this pattern will be copied to some specified directory.

## Prerequisite
You need to have java installed on your system. You can get the java from https://www.oracle.com/technetwork/java/javase/downloads/index.html.

## Usage
Download: Copier.java, DiskSearcher.java, Scouter.java, Searcher.java AND SynchronizedQueue.java and put them in the same folder.
Than, run the following commands in the cmd\terminal:
````
javac DiskSearcher.java
````
````
java DiskSearcher <filename-pattern> <root directory> <destination directory>
<# of searchers> <# of copiers>
````
for example:
````
java DiskSearcher solution /Users/idan/Desktop /Users/idan/Desktop/temp/ 10 5
````

This will run the search application to look for file names with the string “solution”, in the directory /Users/idan/Desktop and all of its subdirectories. Any matched file will be copied to /Users/idan/Desktop/temp. The application will use 10 searcher threads and 5 copier threads.
