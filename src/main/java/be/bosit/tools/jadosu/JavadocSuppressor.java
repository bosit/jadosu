package be.bosit.tools.jadosu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 1:22
 */
public class JavadocSuppressor {

    private List<SourceCode> sourceCodes = new ArrayList<SourceCode>();
    private Map<SourceCode, SourceCode> processedFiles = new HashMap<SourceCode, SourceCode>();

    public int countSourceFiles() {
        return sourceCodes.size();
    }

    public void addSourceFile(SourceCode sourceCode) {
        sourceCodes.add(sourceCode);
    }

    public void process() {
        for (SourceCode sourceCodeText : sourceCodes) {
            final SourceCode original = sourceCodeText;
            final SourceCode processed = original.deleteComments();
            processedFiles.put(original, processed);
        }
    }

    public Map<SourceCode, SourceCode> getProcessedFiles() {
        return processedFiles;
    }

    public void addSourceFiles(List<SourceCode> files) {
        sourceCodes.addAll(files);
    }

}
