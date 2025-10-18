
package dogapi;

import java.util.*;
/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // Instance of the underlying fetcher that does the actual API/database calls
    private final BreedFetcher underlyingFetcher;

    // The cache mapping breed name (String) to its list of sub-breeds (List<String>)
    private final Map<String, List<String>> cache;

    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlyingFetcher = fetcher;
        this.cache = new HashMap<>();

    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // 1. Check if the result is already in the cache (cache hit)
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // 2. Not in cache (cache miss), call the underlying fetcher
        try {
            // CRITICAL FIX: Increment the counter here because a call to the underlying
            // fetcher is about to be made, regardless of whether it succeeds or fails.
            // This satisfies the test expectation that the call is counted even when
            // BreedNotFoundException is thrown.
            callsMade++;

            // Execute the fetch
            List<String> subBreeds = underlyingFetcher.getSubBreeds(breed);

            // 3. Success: Cache the result and return it.
            cache.put(breed, subBreeds);
            return subBreeds;

        } catch (BreedNotFoundException e) {
            // 4. Failure: The call was counted above. DO NOT cache the result, and re-throw the exception.
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }

}
