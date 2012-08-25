package be.bosit.tools.jadosu;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
* User: Jonathan Bosmans
* Date: 25/08/12
* Time: 19:04
*/
public class ConsoleRuntime {
    private String[] args;
    private final Options options;
    private final Parser parser;
    private CommandLine commandLine;
    private final SourceFileDetector detector;
    private final TextFileExtensionFilter fileFilter;
    private final JavadocSuppressor suppressor;

    public ConsoleRuntime(String... args) {
        this.args = args;
        options = new Options();
        createOptions();
        parser = new BasicParser();
        detector = new SourceFileDetector();
        fileFilter = new TextFileExtensionFilter();
        fileFilter.setValidFileExtensions(new HashSet<String>(Arrays.asList("java")));
        detector.setSourceFileFilter(fileFilter);
        suppressor = new JavadocSuppressor();
    }

    public void run() {
        parseArguments();
        if(isHelpState()){
            HelpFormatter f = new HelpFormatter();
            f.printHelp("jadosu -s source_directory_path -t target_directory_path [-i]", options);
            return;
        }
        validateArguments();
        final File sourceDir = getSourceDirectory();
        final File targetDir = getTargetDir();
        writeConsole("Scanning " + sourceDir.getPath() + "...");
        detector.addFileToScan(sourceDir);
        writeConsole("Detected " + detector.getFilesToScan().size() + " files to process.");
        suppressor.addSourceFiles(SourceCodeFile.forFiles(detector.getFilesToScan()));
        writeConsole("Processing...");
        suppressor.process();
        writeConsole("Removed comments from " + suppressor.getProcessedFiles().size() + " files");
        copyProcessedFiles(sourceDir, targetDir);
        if(commandLine.hasOption("i")){
            copyUnprocessedFiles(sourceDir, targetDir);
        }
    }

    private void copyUnprocessedFiles(File sourceDir, File targetDir) {
        final Set<SourceCode> sourceCodes = suppressor.getProcessedFiles().keySet();
        Set<String> processedSourcePaths = extractFilePaths(sourceCodes);
        TreeSet<String> allSourcePaths = new TreeSet<String>(extractPaths(FileUtils.listFilesAndDirs(getSourceDirectory(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)));
        String sourcePath = sourceDir.getPath();
        String targetPath = targetDir.getPath();
        for(String source : allSourcePaths){
            if(!processedSourcePaths.contains(source)){
                File sourceFile = new File(source);
                String target = sourceFile.getPath().replaceFirst(Pattern.quote(sourcePath), targetPath);
                File targetFile = new File(target);
                if(sourceFile.isDirectory() && !targetFile.exists()){
                    targetFile.mkdirs();
                }else{
                    File targetFileParent = targetFile.getParentFile();
                    if(!targetFileParent.exists())
                        targetFileParent.mkdirs();
                    if(!targetFile.exists())
                        try {
                            FileUtils.copyFile(sourceFile, targetFile, true);
                        } catch (IOException e) {
                            throw new FileCopyException(sourceFile, targetFile, e);
                        }
                }
            }
        }

    }

    private Set<String> extractPaths(Collection<File> files) {
        Set<String> paths = new HashSet<String>();
        for (File file : files) {
            paths.add(file.getPath());
        }
        return paths;
    }

    private Set<String> extractFilePaths(Set<SourceCode> sourceCodes) {

        Set<String> paths = new HashSet<String>();
        for (SourceCode code : sourceCodes) {
            if(SourceCodeFile.class.isAssignableFrom(code.getClass())){
                String path = ((SourceCodeFile)code).getFile().getPath();
                paths.add(path);
            }
        }
        return paths;
    }

    private void copyProcessedFiles(File sourceDir, File targetDir) {
        SourceCodeFile.writeChangedFilesToDirectory(suppressor, sourceDir, targetDir);
    }

    private boolean isHelpState() {
        return args.length == 0 || commandLine.hasOption("h");
    }

    private void validateArguments() {
        if(!commandLine.hasOption("s")){
            throw new MissingSourceDirectoryArgumentException();
        }
        if(!commandLine.hasOption("t")){
            throw new MissingTargetDirectoryArgumentException();
        }
        final File sourceDir = getSourceDirectory();
        if (!sourceDir.exists() && sourceDir.isDirectory()) {
            throw new CommandLineRunner.InvalidSourceDirectoryException(sourceDir);
        }
        final File targetDir = getTargetDir();
        if (targetDir.exists()) {
            throw new SourceCodeFile.TargetDirectoryPathExistsException(targetDir);
        }
    }

    private File getTargetDir() {
        final String targetDirPath = commandLine.getOptionValue("t");
        return new File(targetDirPath);
    }

    private File getSourceDirectory() {
        final String sourceDirPath = commandLine.getOptionValue("s");
        return new File(sourceDirPath);
    }

    private void parseArguments() {
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            throw new CommandLineRunner.InvalidCommandLineOptionsException(args, e);
        }
    }

    private void writeConsole(String message) {
        System.out.println(message);
    }

    private void createOptions() {
        Option source = new Option("s", "source", true, "The directory containing the sources to be processed");
        source.setRequired(false);
        options.addOption(source);

        Option target = new Option("t", "target", true, "The directory path to be created to hold processed sources");
        target.setRequired(false);
        options.addOption(target);

        Option includeUnprocessed = new Option("i", "includeUnprocessed", false, "Include unprocessed files at the target directory, resulting in a full copy with processed sources");
        includeUnprocessed.setRequired(false);
        options.addOption(includeUnprocessed);

        Option help = new Option("h", "help", false, "Displays help");
        help.setRequired(false);
        options.addOption(help);
    }

    public static class MissingSourceDirectoryArgumentException extends RuntimeException{
        public MissingSourceDirectoryArgumentException() {
            super("The source directory argument is required");
        }
    }

    public static class MissingTargetDirectoryArgumentException extends RuntimeException{
        public MissingTargetDirectoryArgumentException() {
            super("The target directory argument is required");
        }
    }

    public static class FileCopyException extends RuntimeException {
        public FileCopyException(File sourceFile, File targetFile, Exception e) {
            super("Error while copying " + sourceFile.getPath() + " to " + targetFile.getPath(), e);
        }
    }
}
