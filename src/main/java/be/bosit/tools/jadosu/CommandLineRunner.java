package be.bosit.tools.jadosu;

import java.io.File;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 5:18
 */
public class CommandLineRunner {
    public static void main(String[] args) {
        new ConsoleRuntime(args).run();
    }

    public static class InvalidCommandLineOptionsException extends RuntimeException {
        public InvalidCommandLineOptionsException(String[] args, Exception e) {
            super("Could not process args : " + args, e);
        }
    }

    public static class InvalidSourceDirectoryException extends RuntimeException {
        public InvalidSourceDirectoryException(File sd) {
            super("Target dir already exists : " + sd.getPath());
        }
    }

}
