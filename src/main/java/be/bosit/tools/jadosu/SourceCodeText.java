package be.bosit.tools.jadosu;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 1:38
 */
public class SourceCodeText implements SourceCode {

    private final String sourceCode;
    public static final String openTag = "/**";
    public static final String closeTag = "*/";

    public SourceCodeText(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public long getLength() {
        return sourceCode.length();
    }

    public boolean containsOpenJavadocTag() {
        return contains(openTag);
    }

    public boolean containsCloseJavadocTag() {
        return contains(closeTag);
    }

    public SourceCodeText deleteComments() {
        if(!containsOpenJavadocTag()){
            return this;
        }else{
            String withoutJavadoc = removeJavadoc();
            return  new SourceCodeText(withoutJavadoc);
        }
    }

    private String removeJavadoc() {
        return new JavadocTextEraser().processSourceCode(SourceCodeText.this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SourceCode))
            return false;

        SourceCodeText that = (SourceCodeText) o;

        if (sourceCode != null ? !sourceCode.equals(that.sourceCode) : that.sourceCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sourceCode != null ? sourceCode.hashCode() : 0;
    }

    public boolean contains(String word) {
        return sourceCode.contains(word);
    }

    public String toString()
    {
        return sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void writeToFile(File file) {
        try {
            FileUtils.write(file, sourceCode);
        } catch (IOException e) {
            throw new WriteSourceToFileException(file, e);
        }
    }

    public static class WriteSourceToFileException extends RuntimeException{
        public WriteSourceToFileException(File file, Exception e) {
            super("Could not write sourcecode to file " + file.getPath(), e);
        }
    }
}
