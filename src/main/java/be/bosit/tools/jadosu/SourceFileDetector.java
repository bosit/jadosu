package be.bosit.tools.jadosu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 3:43
 */
public class SourceFileDetector {

    private List<File> filesToScan = new ArrayList<File>();

    private Filter<File> sourceFileFilter = new TextFileExtensionFilter();

    public int countFilesToScan() {
        return filesToScan.size();
    }

    public void addFileToScan(File file) {
        if(sourceFileFilter.accepts(file)){
            filesToScan.add(file);
        }else if(file.isDirectory()){
            addDirectoryToScan(file);
        }else{
            //System.out.println("Unknown : " + file.getPath());

        }
    }

    private void addDirectoryToScan(File directory) {
        Set<File> directories = new HashSet<File>();
        directories.add(directory);
        while(!directories.isEmpty()){
            final Iterator<File> it = directories.iterator();

            final File currentDirectory = it.next();
            it.remove();
            for(File uf : currentDirectory.listFiles()){
                if(uf.isDirectory()) {
                    directories.add(uf);
                } else if(uf.isFile()) {
                    addFileToScan(uf);
                } else {
                    //System.out.println("Unknown : " + uf.getPath());
                }
            }
        }
    }

    public void setSourceFileFilter(Filter<File> sourceFileFilter) {
        this.sourceFileFilter = sourceFileFilter;
    }

    public List<File> getFilesToScan() {
        return filesToScan;
    }
}
