package be.bosit.tools.jadosu;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 1:23
 */
public class JavadocSuppressorTest {

    private JavadocSuppressor suppressor;
    private File fileTapRule;
    private File fileTelehashFactory;
    private String tapRuleText;
    private SourceCode tapRuleSources;
    private String hashFactoryText;
    private SourceCode hashFactorySources;

    @Before
    public void setUp() throws Exception {
        suppressor = new JavadocSuppressor();
        String projectRootDir = FileHelpers.getProjectRootDirectoryPath();
        fileTapRule = new File(projectRootDir + "/src/test/resources/TapRule.java");
        fileTelehashFactory = new File(projectRootDir + "/src/test/resources/TelehashFactory.java");
        tapRuleText = readFileToString(fileTapRule);
        tapRuleSources = new SourceCodeText(tapRuleText);
        hashFactoryText = readFileToString(fileTelehashFactory);
        hashFactorySources = new SourceCodeText(hashFactoryText);
    }

    @Test
    public void newInstance_hasZeroSourceFileCount() throws Exception{
        assertEquals(0, suppressor.countSourceFiles());
    }

    @Test
    public void addingSourceFile_incrementsSourceFileCount() throws Exception{
        suppressor.addSourceFile(new SourceCodeText(""));
        assertEquals(1, suppressor.countSourceFiles());
    }
    
    @Test
    public void processingWithoutFiles_doesNothing() throws Exception{
        suppressor.process();
        Map<SourceCode, SourceCode> processedFiles = suppressor.getProcessedFiles();
        assertEquals(0, processedFiles.size());
    }

    @Test
    public void processingSourceFileWithJavadoc_removesJavadoc() throws Exception{
        assertTrue(tapRuleSources.containsOpenJavadocTag());
        assertTrue(hashFactorySources.containsOpenJavadocTag());
        suppressor.addSourceFile(tapRuleSources);
        suppressor.addSourceFile(hashFactorySources);
        suppressor.process();
        final Map<SourceCode, SourceCode> processedFiles = suppressor.getProcessedFiles();
        assertEquals(2, processedFiles.size());
        assertFalse(processedFiles.get(tapRuleSources).containsOpenJavadocTag());
        assertFalse(processedFiles.get(hashFactorySources).containsOpenJavadocTag());
    }

    @Test
    public void testAddingMultipleFiles() throws Exception{
        List<SourceCode> files = Arrays.asList(tapRuleSources, hashFactorySources);
        suppressor.addSourceFiles(files);
        assertEquals(2, suppressor.countSourceFiles());
    }

    @Test
    public void testSuppressingDirectory() throws Exception{
        final SourceFileDetector detector = createJavaSourcesDetector();
        detector.addFileToScan(new File("/home/spectre/Projects/TeleHash/java"));
        final List<SourceCode> list = SourceCodeFile.forFiles(detector.getFilesToScan());
        assertTrue(list.size() > 10);
        suppressor.addSourceFiles(list);
        suppressor.process();
        final Map<SourceCode, SourceCode> p = suppressor.getProcessedFiles();
        final Map<SourceCode, SourceCode> processedFiles = new TreeMap<SourceCode, SourceCode>(new Comparator<SourceCode>() {
            public int compare(SourceCode o1, SourceCode o2) {
                return ((SourceCodeFile)o1).getFile().getPath().compareTo(((SourceCodeFile)o2).getFile().getPath());
            }
        });
        processedFiles.putAll(p);
        for (Map.Entry<SourceCode, SourceCode> entry : processedFiles.entrySet()) {
            final SourceCodeFile originalFile = (SourceCodeFile) entry.getKey();
            System.out.println("Removed comments from " + originalFile.getFile().getPath() + " : " + originalFile.getLength() + "=>" + entry.getValue().getLength());
        }
    }

    public static SourceFileDetector createJavaSourcesDetector() {
        final SourceFileDetector detector = new SourceFileDetector();
        final TextFileExtensionFilter sourceFileFilter = new TextFileExtensionFilter();
        sourceFileFilter.setValidFileExtensions(new HashSet<String>(Arrays.asList("java")));
        detector.setSourceFileFilter(sourceFileFilter);
        return detector;
    }

    @Test
    public void testStoreChangedFilesToNewDirectory() throws Exception{

        final SourceFileDetector detector = createJavaSourcesDetector();
        final File sourceDir = new File("/home/spectre/Projects/TeleHash/java");
        detector.addFileToScan(sourceDir);
        final List<SourceCode> list = SourceCodeFile.forFiles(detector.getFilesToScan());
        assertTrue(list.size() > 10);
        suppressor.addSourceFiles(list);
        suppressor.process();
        final Map<SourceCode, SourceCode> p = suppressor.getProcessedFiles();
        final Map<SourceCode, SourceCode> processedFiles = new TreeMap<SourceCode, SourceCode>(new Comparator<SourceCode>() {
            public int compare(SourceCode o1, SourceCode o2) {
                return ((SourceCodeFile)o1).getFile().getPath().compareTo(((SourceCodeFile)o2).getFile().getPath());
            }
        });
        processedFiles.putAll(p);
        for (Map.Entry<SourceCode, SourceCode> entry : processedFiles.entrySet()) {
            final SourceCodeFile originalFile = (SourceCodeFile) entry.getKey();
            System.out.println("Removed comments from " + originalFile.getFile().getPath() + " : " + originalFile.getLength() + "=>" + entry.getValue().getLength());
        }

        final File directory = File.createTempFile("jadosu", "changed_dir");
        directory.delete();
        SourceCodeFile.writeChangedFilesToDirectory(suppressor, sourceDir, directory);
        final SourceFileDetector nd = createJavaSourcesDetector();
        nd.addFileToScan(directory);
        assertEquals(processedFiles.size(), nd.getFilesToScan().size());
        //FileUtils.deleteDirectory(directory);

    }


}
