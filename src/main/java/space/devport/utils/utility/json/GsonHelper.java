package space.devport.utils.utility.json;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ConsoleOutput;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GsonHelper {

    private final Gson gson;

    public GsonHelper(boolean prettyPrinting) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (prettyPrinting)
            gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
    }

    public GsonHelper() {
        this(false);
    }

    /**
     * Asynchronously read ByteBuffer from a file.
     */
    public CompletableFuture<ByteBuffer> read(@NotNull final Path path) {

        AsynchronousFileChannel channel;
        try {
            channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        } catch (IOException e) {
            ConsoleOutput.getInstance().err("Could not open an asynchronous file channel.");
            if (ConsoleOutput.getInstance().isDebug())
                e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> {
                throw new CompletionException(e);
            });
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        CompletableFuture<ByteBuffer> future = new CompletableFuture<>();
        channel.read(buffer, 0, future, new CompletionHandler<Integer, CompletableFuture<ByteBuffer>>() {
            @Override
            public void completed(Integer result, CompletableFuture<ByteBuffer> attachment) {
                attachment.complete(buffer);
            }

            @Override
            public void failed(Throwable exc, CompletableFuture<ByteBuffer> attachment) {
                attachment.completeExceptionally(exc);
            }
        });
        return future;
    }

    public static <T> Type mapList(Class<T> innerType) {
        return TypeToken.getParameterized(List.class, innerType).getType();
    }

    public static <K, V> Type mapMap(Class<K> keyType, Class<V> valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    public static <T> Type map(Class<T> clazz) {
        return new TypeToken<T>() {
        }.getType();
    }

    public <T> T load(String dataPath, Type type) {

        Path path = Paths.get(dataPath);

        if (!Files.exists(path)) return null;

        String input;
        try {
            input = String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (Strings.isNullOrEmpty(input)) return null;

        return gson.fromJson(input, type);
    }

    /**
     * Asynchronously load and parse json from a file.
     *
     * @return CompletableFuture with the parsed output or null
     */
    public <T> CompletableFuture<T> loadAsync(@NotNull final String dataPath, Class<T> inner) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return null;

        Type type = map(inner);

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8);

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    public <T> CompletableFuture<List<T>> loadListAsync(@NotNull final String dataPath, Class<T> innerClazz) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return null;

        Type type = mapList(innerClazz);

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8);

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    public <K, V> CompletableFuture<Map<K, V>> loadMapAsync(@NotNull final String dataPath, Class<K> keyClazz, Class<V> valueClazz) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return null;

        Type type = mapMap(keyClazz, valueClazz);

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8);

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    /**
     * Asynchronously save data to json.
     *
     * @return CompletableFuture with the number of bytes written
     */
    public <T> CompletableFuture<Integer> save(@NotNull final T input, @NotNull final String dataPath) {

        Path path = Paths.get(dataPath);

        AsynchronousFileChannel channel;
        try {
            channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            ConsoleOutput.getInstance().err("Could not open an asynchronous file channel.");
            if (ConsoleOutput.getInstance().isDebug())
                e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> {
                throw new CompletionException(e);
            });
        }

        CompletableFuture<Integer> future = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {

            String jsonString = gson.toJson(input, new TypeToken<T>() {
            }.getType());

            ByteBuffer buffer = ByteBuffer.allocate(jsonString.getBytes().length);
            buffer.put(jsonString.getBytes(StandardCharsets.UTF_8));

            return buffer;
        }).thenAcceptAsync(buffer -> channel.write(buffer, 0, future, new CompletionHandler<Integer, CompletableFuture<Integer>>() {
            @Override
            public void completed(Integer result, CompletableFuture<Integer> attachment) {
                attachment.complete(result);
            }

            @Override
            public void failed(Throwable exc, CompletableFuture<Integer> attachment) {
                future.completeExceptionally(exc);
            }
        }));
        return future;
    }
}