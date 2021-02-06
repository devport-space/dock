package space.devport.dock.utility;

import lombok.Data;

@Data
public class Pair<K, V> {
    private final K key;
    private final V value;
}
