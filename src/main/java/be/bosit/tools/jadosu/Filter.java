package be.bosit.tools.jadosu;

/**
 * User: Jonathan Bosmans
 * Date: 24/08/12
 * Time: 4:03
 */
public interface Filter<T> {
    boolean accepts(T item);
}
