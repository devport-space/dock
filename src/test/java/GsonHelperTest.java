import org.junit.Test;
import space.devport.utils.utility.json.GsonHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GsonHelperTest {

    @Test
    public void gsonHelperShouldMapCorrectly() {
        GsonHelper gsonHelper = new GsonHelper();

        int input = 64;

        gsonHelper.save(input, "./test-data.json");

        gsonHelper.loadAsync("./test-data.json", Integer.class).thenAcceptAsync(output -> {
            assertEquals((int) output, input);
        });
    }

    @Test
    public void gsonHelperShouldMapListsCorrectly() {
        GsonHelper gsonHelper = new GsonHelper();

        List<Integer> input = Arrays.asList(1, 2, 3, 4);

        gsonHelper.save(input, "./test-data.json");

        gsonHelper.loadListAsync("./test-data.json", Integer.class).thenAcceptAsync(output -> {
            for (int n = 0; n < input.size(); n++) {
                assertEquals(output.get(n), input.get(n));
            }
        });
    }

    @Test
    public void gsonHelperShouldMapMapsCorrectly() {
        GsonHelper gsonHelper = new GsonHelper();

        Map<Integer, Integer> input = new HashMap<Integer, Integer>() {{
            put(0, 1);
            put(1, 2);
            put(3, 5);
            put(8, 13);
        }};

        gsonHelper.save(input, "./test-data.json");

        gsonHelper.loadMapAsync("./test-data.json", Integer.class, Integer.class).thenAcceptAsync(output -> {
            for (Map.Entry<Integer, Integer> entry : input.entrySet()) {
                assertEquals(entry.getValue(), output.get(entry.getKey()));
            }
        });
    }
}
