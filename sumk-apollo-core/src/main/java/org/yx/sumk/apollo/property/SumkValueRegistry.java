package org.yx.sumk.apollo.property;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.yx.main.SumkThreadPool;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : wjiajun
 */
public class SumkValueRegistry {

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    /**
     * key : sumk_value = 1 : n
     */
    private static final Multimap<String, SumkValue> REGISTRY = Multimaps.synchronizedListMultimap(LinkedListMultimap.create());

    public void register(String key, SumkValue sumkValue) {
        REGISTRY.put(key, sumkValue);

        if (initialized.compareAndSet(false, true)) {
            initialize();
        }
    }

    public static Collection<SumkValue> get(String key) {
        return REGISTRY.get(key);
    }

    private void initialize() {
        SumkThreadPool.scheduleAtFixedRate(() -> {
            try {
                scanAndClean();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(5));
    }

    private void scanAndClean() {
        while (!Thread.currentThread().isInterrupted() && !REGISTRY.entries().isEmpty()) {
            // clear unused sumk values
            REGISTRY.entries().removeIf(value -> !value.getValue().isTargetBeanValid());
        }
    }
}

