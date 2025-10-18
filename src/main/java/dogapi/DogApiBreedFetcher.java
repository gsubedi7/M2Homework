package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        // 1. Construct the API Request URL: https://dog.ceo/api/breed/{breed}/list
        String apiUrl = String.format("https://dog.ceo/api/breed/%s/list", breed.toLowerCase());
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        // 2. Make the API Call and handle response
        try (Response response = client.newCall(request).execute()) {

            // Check for non-successful HTTP status codes
            if (!response.isSuccessful()) {
                // All failures must throw BreedNotFoundException
                throw new BreedNotFoundException("API call failed with HTTP code: " + response.code());
            }

            // Ensure the response body exists
            if (response.body() == null) {
                // All failures must throw BreedNotFoundException
                throw new BreedNotFoundException("API response body was empty.");
            }

            String jsonString = response.body().string();
            JSONObject json = new JSONObject(jsonString);

            // 3. Parse JSON response and check the 'status' field
            String status = json.getString("status");
            if (!"success".equals(status)) {
                // Failure 3: API reported an error status (e.g., breed not found)
                String message = json.optString("message", "Breed not found or API error.");
                throw new BreedNotFoundException(message);
            }

            // Extract the sub-breeds array from the "message" field
            JSONArray subBreedsArray = json.getJSONArray("message");
            List<String> subBreedsList = new ArrayList<>();

            // Convert JSONArray elements (Strings) to List<String>
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreedsList.add(subBreedsArray.getString(i));
            }

            return subBreedsList;

        } catch (IOException e) {
            // Failure 4: Catch checked exceptions (IOException for network)
            // Wrap the IOException in BreedNotFoundException
            throw new BreedNotFoundException("Network or I/O error during API call.");
        } catch (JSONException e) {
            // Failure 4: Catch JSON parsing errors
            // Wrap the JSONException in BreedNotFoundException
            throw new BreedNotFoundException("Error processing API response structure.");
        }
    }
}
