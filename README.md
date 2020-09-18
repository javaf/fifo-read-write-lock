FIFO Read-write Lock uses a counter and a
boolean flag to keep track of multiple readers
and waiting writer. So, when a writer wants to
enter CS, ans new readers are blocked. This
prevents writer lock-out due to continual
stream of readers. This happens because
readers are usually more common.

A common lock is used to ensure internal
updates happen atomically and a common
condition is used for indicating either "no
reader" or "no writer".

Acquiring the read lock involves holding the
common lock, waiting until there writer wanting to enter CS (or already entered), and finally
incrementing the readers count. Releasing the
read lock involves holding the common lock,
decrementing the reader count, and signalling
any writer/readers.

Acquiring the write lock involves holding the
common lock, waiting until there are no writers
wanting to enter CS (or already in CS),
indicating desire to enter, and finally waiting
until there are no readers. Releasing the write
lock involves holding the common lock,
indicating absence of writer, and signalling any
writer/readers.

As mentioned before, this prevents writer
lock-out. However, if there are several
writers, there is no prioritization among
them. This can cause some writers to be
waiting for long in the presence of a large
number of writers. Thus, this read-write lock is
suitable when there are a small number of
writers.

```java
readLock().lock():
1. Acquire common lock.
2. Wait until no writer wants CS.
3. Increment readers count.
4. Release common lock.
```

```java
readLock().unlock():
1. Acquire common lock.
2. Decrement readers count.
3. If no readers, signal any writer/readers.
4. Release common lock.
```

```java
writeLock().lock():
1. Acquire common lock.
2. Wait until there is no writer.
3. Indicate you want to access CS.
4. Wait until there is no reader.
5. Release common lock.
```

```java
writeLock().unlock():
1. Acquire common lock.
2. Indicate absence of writer.
3. Signal any writer/readers.
4. Release common lock.
```

See [FifoReadWriteLock.java] for code, [Main.java] for test, and [repl.it] for output.

[FifoReadWriteLock.java]: https://repl.it/@wolfram77/fifo-read-write-lock#FifoReadWriteLock.java
[Main.java]: https://repl.it/@wolfram77/fifo-read-write-lock#Main.java
[repl.it]: https://fifo-read-write-lock.wolfram77.repl.run


### references

- [The Art of Multiprocessor Programming :: Maurice Herlihy, Nir Shavit](https://dl.acm.org/doi/book/10.5555/2385452)
