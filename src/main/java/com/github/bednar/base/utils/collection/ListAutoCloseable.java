package com.github.bednar.base.utils.collection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (19/09/2013 8:03 PM)
 */
public class ListAutoCloseable<E extends AutoCloseable> implements List<E>, AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(ListAutoCloseable.class);

    private final List<E> delegate;

    public ListAutoCloseable()
    {
        this(Lists.<E>newArrayList());
    }

    public ListAutoCloseable(final @Nonnull List<E> delegate)
    {
        Preconditions.checkNotNull(delegate);

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
    public boolean contains(final Object o)
    {
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator()
    {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a)
    {
        return delegate.toArray(a);
    }

    public boolean add(final E e)
    {
        return delegate.add(e);
    }

    @Override
    public boolean remove(final Object o)
    {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return delegate.containsAll(c);
    }

    public boolean addAll(final Collection<? extends E> c)
    {
        return delegate.addAll(c);
    }

    public boolean addAll(final int index, final Collection<? extends E> c)
    {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c)
    {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c)
    {
        return delegate.retainAll(c);
    }

    @Override
    public void clear()
    {
        delegate.clear();
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

    @Override
    public E get(final int index)
    {
        return delegate.get(index);
    }

    public E set(final int index, final E element)
    {
        return delegate.set(index, element);
    }

    public void add(final int index, final E element)
    {
        delegate.add(index, element);
    }

    @Override
    public E remove(final int index)
    {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(final Object o)
    {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o)
    {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator()
    {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index)
    {
        return delegate.listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex)
    {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public void close()
    {
        if (isEmpty())
        {
            return;
        }

        for (AutoCloseable autoCloseable : this)
        {
            try
            {
                autoCloseable.close();
            }
            catch (Exception e)
            {
                LOG.error("[cannot-close-list-item]", e);
            }
        }
    }
}
