package net.barttolo.reliquias;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/** Identity-based weak keys: effect duration changes must not change the key hash. */
final class WeakIdentitySet<T> {
  private final ReferenceQueue<T> queue = new ReferenceQueue<>();
  private final Set<Key<T>> keys = new HashSet<>();

  boolean add(T value) {
    for (var ref = queue.poll(); ref != null; ref = queue.poll()) keys.remove(ref);
    return keys.add(new Key<>(value, queue));
  }

  private static final class Key<T> extends WeakReference<T> {
    private final int hash;

    Key(T value, ReferenceQueue<T> queue) {
      super(value, queue);
      hash = System.identityHashCode(value);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      return o instanceof Key<?> k && get() != null && get() == k.get();
    }
  }
}
