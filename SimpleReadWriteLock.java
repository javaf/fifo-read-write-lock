import java.util.concurrent.locks.*;

// Simple Read-write Lock uses a counter and a
// boolean flag to keep track of multiple readers
// and a writer, but does not prioritize writers.
// A common lock is used to ensure internal
// updates happen atomically and a common
// condition is used for indicating either "no
// reader" or "no writer".
// 
// Acquiring the read lock involves holding the
// common lock, waiting until there is no writer,
// and finally incrementing the readers count.
// Releasing the read lock involves holding the
// common lock, decrementing the reader count, and
// signalling any writer/readers.
// 
// Acquiring the write lock involves holding the
// common lock, waiting until there are no writers
// and readers, and finally indicating presence of
// a writer. Releasing the write lock involves
// involves holding the common lock, indicating
// absence of writer, and signalling any
// writer/readers.
// 
// Even though the algorithm is correct, it is not
// quite satisfactory. If readers are much more
// frequent than writers, as is usually the case,
// the writers could be locked out for a long
// period of time by a continual stream of readers.
// Due to this lack of writer prioritization, this
// type of lock is generally only suitable for
// educational purposes.

class SimpleReadWriteLock implements ReadWriteLock {
  Lock lock;
  Condition condition;
  Lock readLock, writeLock;
  int readers;
  boolean writer;
  // lock: common lock
  // condition: indicates "no reader"/"no writer"
  // readers: number of readers accessing
  // writer: indicates if writer is accessing

  public SimpleReadWriteLock() {
    lock = new ReentrantLock();
    condition = lock.newCondition();
    readLock = new ReadLock();
    writeLock = new WriteLock();
    readers = 0;
    writer = false;
  }

  @Override
  public Lock readLock() {
    return readLock;
  }

  @Override
  public Lock writeLock() {
    return writeLock;
  }
  

  class ReadLock extends AbstractLock {
    // 1. Acquire common lock.
    // 2. Wait until there is no writer.
    // 3. Increment readers count.
    // 4. Release common lock.
    @Override
    public void lock() {
      lock.lock(); // 1
      try {
        while (writer) condition.await(); // 2
        readers++; // 3
      }
      catch (InterruptedException e) {}
      finally { lock.unlock(); } // 4
    }
  
    // 1. Acquire common lock.
    // 2. Decrement readers count.
    // 3. If no readers, signal any writer/readers.
    // 4. Release common lock.
    @Override
    public void unlock() {
      lock.lock(); // 1
      readers--; // 2
      if (readers == 0) condition.signalAll(); // 3
      lock.unlock(); // 4
    }
  }


  class WriteLock extends AbstractLock {
    // 1. Acquire common lock.
    // 2. Wait until there is no writer, reader.
    // 3. Indicate presence of writer.
    // 4. Release common lock.
    @Override
    public void lock() {
      lock.lock(); // 1
      try {
        while (writer || readers > 0) // 2
          condition.await();          // 2
        writer = true; // 3
      }
      catch (InterruptedException e) {}
      finally { lock.unlock(); } // 4
    }
  
    // 1. Acquire common lock.
    // 2. Indicate absence of writer.
    // 3. Signal any writer/readers.
    // 4. Release common lock.
    @Override
    public void unlock() {
      lock.lock(); // 1
      writer = false; // 2
      condition.signalAll(); // 3
      lock.unlock(); // 4
    }
  }
}

