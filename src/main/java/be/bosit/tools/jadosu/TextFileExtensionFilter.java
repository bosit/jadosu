package be.bosit.tools.jadosu;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 4:04
 */
public class TextFileExtensionFilter implements Filter<File> {

    private Set<String> validFileExtensions = new HashSet<String>();

    public boolean accepts(File item) {
        final String fileExtension = extractFileExtension(item.getName());
        if(!item.isFile() || fileExtension == null) return false;
        return validFileExtensions.contains(fileExtension);
    }

    private String extractFileExtension(String name) {
        final int dotIdx = name.lastIndexOf(".");
        if(dotIdx <= 0 || dotIdx==(name.length()-1)) return null;
        return name.substring(dotIdx+1);
    }

    public void setValidFileExtensions(Set<String> validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
    }
}
