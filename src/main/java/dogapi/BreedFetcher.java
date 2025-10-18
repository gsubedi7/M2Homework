package dogapi;

import java.util.List;

/**
 * Interface for the service of getting sub breeds of a given dog breed.
 */
public interface BreedFetcher {

    /**
     * Fetch the list of sub breeds for the given breed.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist
     */
    List<String> getSubBreeds(String breed) throws BreedNotFoundException;


    // TODO Task 4: make this a checked exception and update any other code as needed.
    // By changing it to extend Exception, it becomes a checked exception.
    // The method signature above has been updated to declare the throw.
    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String breed) {
            super("Breed not found: " + breed);
        }
    }
}
