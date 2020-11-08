package space.devport.utils.utility;

import lombok.Data;

@Data
public class Pair<K, V> {
    private final K key;
    private final V value;
}
