package com.github.bednar.base.utils.collection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (19/09/2013 8:29 PM)
 */
public class MapDefaultValue<K, V> implements Map<K, V>
{
    private static final Logger LOG = LoggerFactory.getLogger(MapDefaultValue.class);

    private final Supplier<V> supplier;
    private final Map<K, V> delegate;

    public MapDefaultValue(final @Nonnull Supplier<V> supplier, final @Nonnull Map<K, V> delegate)
    {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(delegate);

        this.supplier = supplier;
        this.delegate = delegate;
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return delegate.containsValue(value);
    }

    @Override
    @Nonnull
    public V get(final Object key)
    {
        V value = delegate.get(key);
        if (value == null)
        {
            LOG.error("[cannot-find-value-return-default][{}]", this.supplier);

            return this.supplier.get();
        }

        return value;
    }

    public V put(final K key, final V value)
    {
        return delegate.put(key, value);
    }

    @Override
    public V remove(final Object key)
    {
        return delegate.remove(key);
    }

    public void putAll(final Map<? extends K, ? extends V> m)
    {
        delegate.putAll(m);
    }

    @Override
    public void clear()
    {
        delegate.clear();
    }

    @Override
    public Set<K> keySet()
    {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values()
    {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(final Object o)
    {
        return delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return delegate.hashCode();
    }
}
