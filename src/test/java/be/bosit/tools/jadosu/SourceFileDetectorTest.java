package be.bosit.tools.jadosu;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static be.bosit.tools.jadosu.FileHelpers.getProjectRootDirectoryPath;
import static junit.framework.Assert.assertEquals;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 3:43
 */
public class SourceFileDetectorTest {

    private SourceFileDetector scanner;
    private File directory;
    private File file;

    @Before
    public void setUp() throws Exception {
        final String projectRootDir = getProjectRootDirectoryPath();
        directory = new File(projectRootDir + "/src/test/resources");
        file = new File(projectRootDir + "/src/test/java/be/bosit/tools/jadosu/SourceFileDetectorTest.java");
        scanner = new SourceFileDetector();
        final TextFileExtensionFilter filter = new TextFileExtensionFilter();
        filter.setValidFileExtensions(new HashSet<String>(Arrays.asList("java")));
        scanner.setSourceFileFilter(filter);
    }

    @Test
    public void newInstance_zeroFileCount() throws Exception{
        int count = scanner.countFilesToScan();
        assertEquals(0, count);
    }

    @Test
    public void addingFileToScan_incrementsCount() throws Exception{
        scanner.addFileToScan(file);
        assertEquals(1, scanner.countFilesToScan());
    }

    @Test
    public void addingDirectoryToScan_addFilesInDirectory() throws Exception{
        scanner.addFileToScan(directory);
        assertEquals(2, scanner.countFilesToScan());
    }


}
