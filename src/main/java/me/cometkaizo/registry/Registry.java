package me.cometkaizo.registry;

import me.cometkaizo.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Registry<A, T> {

    private A app;
    private final List<Consumer<T>> registerListeners = new ArrayList<>(1);
    private final Map<String, Function<A, ? extends T>> entryFunctions = new LinkedHashMap<>(5);
    private final Map<String, T> entries = new LinkedHashMap<>(5);
    private Collection<T> entryView = List.of();

    public void addRegisterListener(Consumer<T> listener) {
        registerListeners.add(listener);
    }

    public void removeRegisterListener(Consumer<T> listener) {
        registerListeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <V extends T> Supplier<V> register(String key, Function<A, V> objectFunc) {
        Objects.requireNonNull(objectFunc, "Object function cannot be null");
        Objects.requireNonNull(objectFunc, "Key cannot be null");
        throwIfDuplicateKey(key);

        addEntryFunction(key, objectFunc);
        if (app != null) {
            addEntry(key, objectFunc.apply(app));
            updateEntryView();
        }
        return () -> {
            V value = (V) entries.get(key);
            if (value == null) throw new IllegalStateException("Unknown key '" + key + "'; " + Registry.class.getSimpleName() + "#register(AtmoApp) has not been called");
            return value;
        };
    }

    private void addEntryFunction(String key, Function<A, ? extends T> objectFunc) {
        entryFunctions.put(key, objectFunc);
    }

    private void addEntry(String key, T value) {
        Objects.requireNonNull(value, "Contract violation: cannot add null entry");
        entries.put(key, value);
        registerListeners.forEach(c -> c.accept(value));
    }

    private void throwIfDuplicateKey(String key) {
        Object duplicate = entryFunctions.get(key);
        if (duplicate != null) throw new IllegalArgumentException("Key '" + key + "' already exists for '" + duplicate + "'");
    }

    public void register(A app) {
        this.app = app;
        for (String key : entryFunctions.keySet()) {
            var valueFunc = entryFunctions.get(key);
            addEntry(key, valueFunc.apply(app));
        }
        updateEntryView();
    }

    private void updateEntryView() {
        entryView = Collections.unmodifiableCollection(entries.values());
    }

    public T getValue(String key) {
        if (app == null) throw new IllegalStateException("Unknown key '" + key + "'; " + Registry.class.getSimpleName() + "#register(AtmoApp) has not been called");
        T result = entries.get(key);
        if (result == null) throw new NoSuchElementException("Unknown key '" + key + "'; available entries are: \n" + entries);
        return result;
    }

    public String getKey(T value) {
        if (app == null) throw new IllegalStateException("Unknown key '" + value + "'; " + Registry.class.getSimpleName() + "#register(AtmoApp) has not been called");
        var entry = CollectionUtils.getFirst(entries.entrySet(), e -> e.getValue() == value);
        if (entry.isEmpty()) throw new NoSuchElementException("Unknown value '" + value + "'; available entries are: \n" + entries);
        return entry.get().getKey();
    }

    public Collection<T> values() {
        return entryView;
    }

}
