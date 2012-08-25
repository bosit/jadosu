package be.bosit.tools.jadosu;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 3:06
 */
public class SourceCodeFile extends SourceCodeText{

    private final File file;

    public SourceCodeFile(File file) {
        super(readFileToString(file));
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public SourceCodeText deleteComments() {
        final String withoutComments = new JavadocTextEraser().processSourceCode(this);
        return new SourceCodeText(withoutComments);
    }

    public static String readFileToString(File file) {
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new ReadTextFileException(file, e);
        }
    }

    public static List<SourceCode> forFiles(List<File> filesToScan) {
        List<SourceCode> files = new ArrayList<SourceCode>(filesToScan.size());
        for(File f : filesToScan){
            files.add(new SourceCodeFile(f));
        }
        return files;
    }

    public static void writeChangedFilesToDirectory(JavadocSuppressor suppressor, File sourceDir, File directory) {
        if(directory.exists())
            throw new TargetDirectoryPathExistsException(directory);
        directory.mkdir();
        final String baseDirPath = discoverBaseDir(suppressor.getProcessedFiles().keySet());
        final String basePathQuoted = Pattern.quote(baseDirPath);
        System.out.println("BaseDir = " + baseDirPath + ", writing to "+ directory.getPath());
        int unknownIdx = 0;
        for (Map.Entry<SourceCode, SourceCode> entry : suppressor.getProcessedFiles().entrySet()) {
            final File targetFile;
            if(SourceCodeFile.class.isAssignableFrom(entry.getKey().getClass())){
                final String path = ((SourceCodeFile) entry.getKey()).getFile().getPath().replaceFirst(basePathQuoted, directory.getPath());

                targetFile = new File(path);
            }else{
                targetFile= new File(directory, "unknown_" + unknownIdx + ".txt");
                unknownIdx++;
            }
            try {
                if(targetFile.getPath().startsWith("/home")) throw new IllegalStateException("not allowed to write to " + targetFile.getPath());
                FileUtils.write(targetFile, entry.getValue().getSourceCode());
            } catch (IOException e) {
                throw new WriteSourceToFileException(targetFile, e);
            }
        }
    }

    public static String discoverBaseDir(Set<SourceCode> sourceCodes) {
        String maxCommon = null;
        for(SourceCode c : sourceCodes){
            if(SourceCodeFile.class.isAssignableFrom(c.getClass())){
                final SourceCodeFile codeFile = (SourceCodeFile) c;
                final String codeFilePath = codeFile.getFile().getPath();
                if(maxCommon == null){
                    maxCommon = codeFilePath;
                }else{
                    maxCommon = extractCommonPart(maxCommon, codeFilePath);
                    if(maxCommon == null) return null;
                }

            }
        }
        if(maxCommon.substring(maxCommon.length()-1).equals(File.separator))
            return maxCommon.substring(0, maxCommon.length()-1);
        return maxCommon;
    }

    private static String extractCommonPart(String maxCommon, String codeFilePath) {
        StringBuilder common = new StringBuilder();
        for(int i = 0 ; i < maxCommon.length() && i < codeFilePath.length() ; i++){
            if(maxCommon.charAt(i) == codeFilePath.charAt(i)){
                common.append(maxCommon.charAt(i));
            }
        }
        final String commonPart = common.toString();
        if(commonPart.length() < 1 || commonPart.equals(File.separator))
            return null;
        return commonPart;
    }

    public static class ReadTextFileException extends RuntimeException{
        public ReadTextFileException(File file, Exception e) {
            super("Could not read text from file " + file.getPath(), e);
        }
    }

    public static class TargetDirectoryPathExistsException extends RuntimeException{
        public TargetDirectoryPathExistsException(File directory) {
            super("Target dir already exists : " + directory);
        }
    }
}
