package be.bosit.tools.jadosu;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 1:38
 */
public class SourceCodeTextTest {

    private SourceCode sourcesWithoutJavadoc;
    private SourceCodeText sourcesWithJavadoc;
    private final String textWithoutJavadoc = "blah";
    private final String textWithJavadoc = "hello /** test */";
    private final String wordNonJavadoc = "hello";
    private final String wordJavadoc = "test";
    private File writeTestFile;

    @Before
    public void setUp() throws Exception {
        sourcesWithoutJavadoc = new SourceCodeText(textWithoutJavadoc);
        sourcesWithJavadoc = new SourceCodeText(textWithJavadoc);
        writeTestFile = File.createTempFile("jadosu", "fileResult");
        writeTestFile.delete();
    }

    @Test
    public void newInstance_hasZeroLength() throws Exception{
        assertEquals(4, sourcesWithoutJavadoc.getLength());
        assertEquals(17,sourcesWithJavadoc.getLength());
    }

    @Test
    public void setSourceText_updatesLength() throws Exception{
        assertEquals(textWithoutJavadoc.length(), sourcesWithoutJavadoc.getLength());
        assertEquals(textWithJavadoc.length(), sourcesWithJavadoc.getLength());
    }

    @Test
    public void testHasOpenJavadocTag() throws Exception{
        assertFalse(sourcesWithoutJavadoc.containsOpenJavadocTag());
        assertTrue(sourcesWithJavadoc.containsOpenJavadocTag());
    }

    @Test
    public void testHasCloseJavadocTag() throws Exception{
        assertFalse(sourcesWithoutJavadoc.containsCloseJavadocTag());
        assertTrue(sourcesWithJavadoc.containsCloseJavadocTag());
    }

    @Test
    public void deletingCommentsInNonJavadocText_returnsNewInstanceWithSameText() throws Exception{
        final SourceCode result = sourcesWithoutJavadoc.deleteComments();
        assertEquals(sourcesWithoutJavadoc, result);
        assertTrue(sourcesWithoutJavadoc == result);
    }

    @Test
    public void deletingCommentsInJavadocText_returnsNewInstanceWithoutComments() throws Exception{
        final SourceCode result = sourcesWithJavadoc.deleteComments();
        assertFalse(sourcesWithJavadoc.equals(result));
        assertFalse(result.containsOpenJavadocTag());
        assertFalse(result.containsCloseJavadocTag());
    }

    @Test
    public void deletingCommentsInJavadocText_preservesNonJavadocText() throws Exception{
        assertTrue(sourcesWithJavadoc.contains(wordNonJavadoc));
        assertTrue(sourcesWithJavadoc.contains(wordJavadoc));
        final SourceCodeText result = sourcesWithJavadoc.deleteComments();
        assertTrue(result.contains(wordNonJavadoc));
        assertFalse(result.contains(wordJavadoc));
        assertFalse(sourcesWithJavadoc.equals(result));
        assertFalse(result.containsOpenJavadocTag());
        assertFalse(result.containsCloseJavadocTag());
    }

    @Test
    public void sourcesFilesWithSameSource_areEqual() throws Exception{
        assertEquals(sourcesWithoutJavadoc, sourcesWithoutJavadoc);
        assertFalse(sourcesWithoutJavadoc.equals(null));
        assertFalse(sourcesWithoutJavadoc.equals(new Object()));
        SourceCode other = new SourceCodeText("");
        assertFalse(sourcesWithoutJavadoc.equals(other));
        assertFalse(sourcesWithoutJavadoc.equals(sourcesWithJavadoc));
    }

    @Test
    public void toString_returnsSourceCode() throws Exception{
        assertEquals(textWithJavadoc, sourcesWithJavadoc.toString());
    }

    @Test
    public void testWriteToFile() throws Exception{
        System.out.println(writeTestFile.getPath());
        assertFalse(writeTestFile.exists());
        sourcesWithJavadoc.writeToFile(writeTestFile);
        assertTrue(writeTestFile.exists());
        assertEquals(FileUtils.readFileToString(writeTestFile), textWithJavadoc);
        writeTestFile.delete();
    }



}
