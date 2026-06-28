package generalutilities;


import java.util.HashSet;
import java.util.Set;

public final class ThreadLocalRegistry {

    // Thread-local set to store unique ThreadLocal instances per thread
    private static final ThreadLocal<Set<ThreadLocal<?>>> threadRegistry = ThreadLocal.withInitial(HashSet::new);

    // Private constructor to prevent instantiation
    private ThreadLocalRegistry() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Registers a ThreadLocal instance for the current thread.
     * Duplicate registrations are ignored.
     *
     * @param threadLocal the ThreadLocal instance to register
     */
    public static void register(ThreadLocal<?> threadLocal) {
        threadRegistry.get().add(threadLocal); // Set ensures uniqueness
    }

    /**
     * Clears all registered ThreadLocal instances for the current thread.
     */
    public static void clearAll() {
        Set<ThreadLocal<?>> locals = threadRegistry.get();
        for (ThreadLocal<?> tl : locals) {
            tl.remove(); // clears the value for current thread
        }
        locals.clear(); // clear the set
        threadRegistry.remove(); // remove the set itself
    }
}
