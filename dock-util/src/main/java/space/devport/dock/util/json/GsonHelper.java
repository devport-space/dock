package space.devport.dock.util.json;

import space.devport.dock.callbacks.CallbackContent;
import space.devport.dock.callbacks.ExceptionCallback;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

@Log
public class GsonHelper {

    @Getter
    private final GsonBuilder builder = new GsonBuilder();

    private Gson gson;

    public GsonHelper() {
        build();
    }

    public void build() {
        this.gson = builder.create();
    }

    private <T> Type mapList(@NotNull Class<T> innerType) {
        return TypeToken.getParameterized(List.class, innerType).getType();
    }

    private <K, V> Type mapMap(@NotNull Class<K> keyType, @NotNull Class<V> valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    private <T> Type map(@NotNull Class<T> clazz) {
        return new TypeToken<T>() {
        }.getType();
    }

    /*
     * Asynchronously read ByteBuffer from a file.
     */
    @NotNull
    private CompletableFuture<ByteBuffer> read(@NotNull final Path path) {

        AsynchronousFileChannel channel;
        long size;
        try {
            channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            size = channel.size();
        } catch (IOException e) {
            log.severe("Could not open an asynchronous file channel.");
            e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> {
                throw new CompletionException(e);
            });
        }

        if (size > Integer.MAX_VALUE) {
            return CompletableFuture.supplyAsync(() -> {
                throw new CompletionException(new IllegalStateException("File is too big for the reader."));
            });
        }

        ByteBuffer buffer = ByteBuffer.allocate((int) size);

        CompletableFuture<ByteBuffer> future = new CompletableFuture<>();
        channel.read(buffer, 0, future, new CompletionHandler<Integer, CompletableFuture<ByteBuffer>>() {
            @Override
            public void completed(Integer result, CompletableFuture<ByteBuffer> attachment) {
                future.complete(buffer);
            }

            @Override
            public void failed(Throwable exc, CompletableFuture<ByteBuffer> attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future;
    }

    /**
     * Load and parse json from a file.
     *
     * @param <T>      Type signature of loaded json content.
     * @param type     Type of the loaded class.
     * @param dataPath Path to load from.
     * @return Parsed output {@code null}.
     */
    @Nullable
    public <T> T load(@NotNull String dataPath, @NotNull Type type) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return null;

        String input;
        try {
            input = String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (Strings.isNullOrEmpty(input))
            return null;

        return gson.fromJson(input, type);
    }

    /**
     * Asynchronously load and parse json from a file.
     *
     * @param <T>      Type signature of loaded json content.
     * @param type     Type of loaded class.
     * @param dataPath Path to load from.
     * @return CompletableFuture supplying parsed output or supplying {@code null}.
     */
    @NotNull
    public <T> CompletableFuture<T> loadAsync(@NotNull final String dataPath, @NotNull Type type) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return new CompletableFuture<>();

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    /**
     * Asynchronously load and parse list from json.
     *
     * @param <T>        Type signature of list key.
     * @param innerClazz Class defining {@code <T>}.
     * @param dataPath   Path to load from.
     * @return CompletableFuture supplying parsed output list or supplying {@code null}.
     */
    @NotNull
    public <T> CompletableFuture<List<T>> loadListAsync(@NotNull final String dataPath, @NotNull Class<T> innerClazz) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return new CompletableFuture<>();

        final Type type = mapList(innerClazz);

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    /**
     * Asynchronously load and parse a map from json.
     *
     * @param <K>        Type signature of map key.
     * @param <V>        Type signature of map value.
     * @param keyClazz   Class defining {@code <K>}.
     * @param valueClazz Class defining {@code <V>}.
     * @param dataPath   Path to load from.
     * @return CompletableFuture supplying parsed map output or supplying {@code null}.
     */
    @NotNull
    public <K, V> CompletableFuture<Map<K, V>> loadMapAsync(@NotNull final String dataPath, @NotNull Class<K> keyClazz, @NotNull Class<V> valueClazz) {
        Path path = Paths.get(dataPath);

        if (!Files.exists(path))
            return new CompletableFuture<>();

        final Type type = mapMap(keyClazz, valueClazz);

        return read(path).thenApplyAsync(buffer -> {
            String output = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            if (Strings.isNullOrEmpty(output))
                return null;

            return gson.fromJson(output, type);
        });
    }

    /**
     * Synchronously save data to json.
     *
     * @param <T>      Type signature of input.
     * @param input    Input to save.
     * @param dataPath Path to save to.
     */
    public <T> boolean save(@NotNull final String dataPath, @NotNull final T input) {
        return save(dataPath, input, null);
    }

    /**
     * Synchronously save data to json.
     *
     * @param <T>      Type signature of input.
     * @param input    Input to save.
     * @param dataPath Path to save to.
     * @param callback {@link ExceptionCallback} callback to run when an exception is thrown.
     */
    public <T> boolean save(@NotNull final String dataPath, @NotNull final T input, @Nullable ExceptionCallback callback) {
        Path path = Paths.get(dataPath);

        if (!path.toFile().getParentFile().exists()) {
            if (!path.toFile().getParentFile().mkdirs()) {
                log.severe("Could not save, could not create folder structure.");
                return false;
            }
        }

        final Type type = map(input.getClass());

        String jsonString = gson.toJson(input, type).trim();
        try {
            Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            CallbackContent.createNew(e).callOrThrow(callback);
            return false;
        }
    }

    /**
     * Asynchronously save data to json.
     *
     * @param <T>      Type signature of input.
     * @param input    Input to save.
     * @param dataPath Path to save to.
     * @return CompletableFuture supplying the bytes written.
     * Note: calling #join() on this future will hang the main thread.
     */
    @NotNull
    public <T> CompletableFuture<Void> saveAsync(@NotNull final String dataPath, @NotNull final T input) {

        Path path = Paths.get(dataPath);

        if (!path.toFile().getParentFile().exists()) {
            if (!path.toFile().getParentFile().mkdirs()) {
                log.severe("Could not save, could not create folder structure.");
                return CompletableFuture.completedFuture(null);
            }
        }

        AsynchronousFileChannel channel;
        try {
            channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.severe("Could not open an asynchronous file channel.");
            e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> {
                throw new CompletionException(e);
            });
        }

        final Type type = map(input.getClass());

        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            String jsonString = gson.toJson(input, type).trim();

            ByteBuffer buffer = ByteBuffer.allocate(jsonString.getBytes().length);
            buffer.put(jsonString.getBytes(StandardCharsets.UTF_8));
            buffer.flip();

            channel.write(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    future.complete(null);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                }
            });
        });
        return future;
    }
}