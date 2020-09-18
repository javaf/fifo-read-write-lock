Simple Read-write Lock uses a counter and a
boolean flag to keep track of multiple readers
and a writer, but does not prioritize writers.
A common lock is used to ensure internal
updates happen atomically and a common
condition is used for indicating either "no
reader" or "no writer".

Acquiring the read lock involves holding the
common lock, waiting until there is no writer,
and finally incrementing the readers count.
Releasing the read lock involves holding the
common lock, decrementing the reader count, and
signalling any writer/readers.

Acquiring the write lock involves holding the
common lock, waiting until there are no writers
and readers, and finally indicating presence of
a writer. Releasing the write lock involves
involves holding the common lock, indicating
absence of writer, and signalling any
writer/readers.

Even though the algorithm is correct, it is not
quite satisfactory. If readers are much more
frequent than writers, as is usually the case,
the writers could be locked out for a long
period of time by a continual stream of readers.
Due to this lack of writer prioritization, this
type of lock is generally only suitable for
educational purposes.

```java
readLock().lock():
1. Acquire common lock.
2. Wait until there is no writer.
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
2. Wait until there is no writer, reader.
3. Indicate presence of writer.
4. Release common lock.
```

```java
writeLock().unlock():
1. Acquire common lock.
2. Indicate absence of writer.
3. Signal any writer/readers.
4. Release common lock.
```

See [SimpleReadWriteLock.java] for code, [Main.java] for test, and [repl.it] for output.

[SimpleReadWriteLock.java]: https://repl.it/@wolfram77/simple-read-write-lock#SimpleReadWriteLock.java
[Main.java]: https://repl.it/@wolfram77/simple-read-write-lock#Main.java
[repl.it]: https://simple-read-write-lock.wolfram77.repl.run


### references

- [The Art of Multiprocessor Programming :: Maurice Herlihy, Nir Shavit](https://dl.acm.org/doi/book/10.5555/2385452)
