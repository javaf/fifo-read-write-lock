import java.util.concurrent.locks.*;

// FIFO Read-write Lock uses a counter and a
// boolean flag to keep track of multiple readers
// and waiting writer. So, when a writer wants to
// enter CS, ans new readers are blocked. This
// prevents writer lock-out due to continual
// stream of readers. This happens because
// readers are usually more common.
// 
// A common lock is used to ensure internal
// updates happen atomically and a common
// condition is used for indicating either "no
// reader" or "no writer".
// 
// Acquiring the read lock involves holding the
// common lock, waiting until there writer wanting // to enter CS (or already entered), and finally
// incrementing the readers count. Releasing the
// read lock involves holding the common lock,
// decrementing the reader count, and signalling
// any writer/readers.
// 
// Acquiring the write lock involves holding the
// common lock, waiting until there are no writers
// wanting to enter CS (or already in CS),
// indicating desire to enter, and finally waiting
// until there are no readers. Releasing the write
// lock involves holding the common lock,
// indicating absence of writer, and signalling any
// writer/readers.
// 
// As mentioned before, this prevents writer
// lock-out. However, if there are several
// writers, there is no prioritization among
// them. This can cause some writers to be
// waiting for long in the presence of a large
// number of writers. Thus, this read-write lock is
// suitable when there are a small number of
// writers.

class FifoReadWriteLock implements ReadWriteLock {
  Lock lock;
  Condition condition;
  Lock readLock, writeLock;
  int readers;
  boolean writer;
  // lock: common lock
  // condition: indicates "no reader"/"no writer"
  // readers: number of readers accessing
  // writer: indicates if a writer wants access

  public FifoReadWriteLock() {
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
    // 2. Wait until no writer wants CS.
    // 3. Increment readers count.
    // 4. Release common lock.
    @Override
    public void lock() {
      lock.lock(); // 1
      try {
        while (writer) condition.await(); // 2
        readers++; // 3
      } catch (InterruptedException e) {
      } finally {
        lock.unlock();
      } // 4
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
    // 2. Wait until there is no writer.
    // 3. Indicate you want to access CS.
    // 4. Wait until there is no reader.
    // 5. Release common lock.
    @Override
    public void lock() {
      lock.lock(); // 1
      try {
        while (writer) condition.await(); // 2
        writer = true; // 3
        while (readers > 0) condition.await(); // 4
      } catch (InterruptedException e) {
      } finally {
        lock.unlock(); // 5
      }
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
