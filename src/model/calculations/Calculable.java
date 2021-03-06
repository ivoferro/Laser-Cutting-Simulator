/*
 * Package location for Application model concepts.
 */
package model.calculations;

/**
 * Interface for calculations.
 *
 * @author Eric Amaral 1141570
 * @author Daniel Gonçalves 1151452
 * @author Ivo Ferro 1151159
 * @author Tiago Correia 1151031
 */
public interface Calculable {
    
    /**
     * Calculates a given equation. 
     * 
     * @return the result of the calculation as a double.
     */
    double calculate();
}
