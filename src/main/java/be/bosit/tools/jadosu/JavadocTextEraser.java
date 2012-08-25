package be.bosit.tools.jadosu;

/**
* User: Jonathan Bosmans
* Date: 24/08/12
* Time: 3:26
*/
public class JavadocTextEraser {
    public String processSourceCode(SourceCode sourceCode) {
        if(!sourceCode.containsOpenJavadocTag() || !sourceCode.containsCloseJavadocTag()) return sourceCode.getSourceCode();
        StringBuilder b = new StringBuilder(sourceCode.getSourceCode());
        while(builderContainsJavadocTagPair(b)){
            deleteCurrentComment(b);
        }
        return b.toString();
    }

    private boolean builderContainsJavadocTagPair(StringBuilder b) {
        return b.indexOf(SourceCodeText.openTag) >= 0 && b.indexOf(SourceCodeText.closeTag) >0;
    }

    private void deleteCurrentComment(StringBuilder b) {
        final int openIdx = b.indexOf(SourceCodeText.openTag);
        final int closeIdx = b.indexOf(SourceCodeText.closeTag, openIdx + 1);
        b.replace(openIdx, closeIdx+2, "");
    }
}
