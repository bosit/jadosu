package be.bosit.tools.jadosu;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static be.bosit.tools.jadosu.FileHelpers.getProjectRootDirectoryPath;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 3:06
 */
public class SourceCodeFileTest {

    private File tapRuleFile;
    private SourceCodeFile tapRuleSourceFile;

    @Before
    public void setUp() throws Exception {
        tapRuleFile = new File(getProjectRootDirectoryPath() + "/src/test/resources/TapRule.java");
        tapRuleSourceFile = new SourceCodeFile(tapRuleFile);
    }

    @Test
    public void representsExistingFile() throws Exception{
        assertEquals(tapRuleFile, tapRuleSourceFile.getFile());
    }

    @Test
    public void implementsSourceFile() throws Exception{
        assertTrue(SourceCode.class.isAssignableFrom(tapRuleSourceFile.getClass()));
    }

    @Test
    public void toString_returnsContainedText() throws Exception{
        assertEquals(FileUtils.readFileToString(tapRuleFile), tapRuleSourceFile.toString());
    }

    @Test
    public void canDeleteComments() throws Exception{
        assertTrue(tapRuleSourceFile.containsOpenJavadocTag());
        final SourceCode result = tapRuleSourceFile.deleteComments();
        assertFalse(result.containsOpenJavadocTag());
    }

}
