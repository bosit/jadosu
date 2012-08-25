package be.bosit.tools.jadosu;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 3:05
 */
public interface SourceCode {

    long getLength();

    boolean containsOpenJavadocTag();

    boolean containsCloseJavadocTag();

    SourceCode deleteComments();

    String getSourceCode();
}
