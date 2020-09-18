import java.util.concurrent.locks.*;

class Main {
  static ReadWriteLock lock;
  static double[] sharedData;
  static int SD = 100, CS = 10;
  static int RD = 100, WR = 10;
  // SD: shared data array size
  // CS: critical section executed per thread
  // RD: number of readers
  // WR: number of writers

  // Critical section updated shared data
  // with a random value. If all values
  // dont match then it was not executed
  // atomically!
  static void criticalSection() {
    try {
    double v = Math.random();
    for (int i=0; i<SD; i++) {
      if (i % (SD/4) == 0) Thread.sleep(1);
      sharedData[i] = v + v*sharedData[i];
    }
    }
    catch(InterruptedException e) {}
  }

  // Checks to see if all values match. If not,
  // then critical section was not executed
  // atomically.
  static boolean criticalSectionWasAtomic() {
    double v = sharedData[0];
    for (int i=0; i<SD; i++)
      if(sharedData[i] != v) return false;
    return true;
  }

  // Unsafe reader checks CS N times, without
  // holding a lock. This can cause CS check 
  // to be while it is being updated by a
  // writer, which can be detected.
  //
  // Safe reader checks CS N times, while
  // holding a read lock. This allows multiple
  // readers to check CS concurrently, but not
  // while a writer is updating it. Hence, CS
  // executes atomically which can be verified.
  static Thread reader(String id, boolean safe) {
    Thread t = new Thread(() -> {
      for (int i=0; i<CS; i++) {
        if (safe) lock.readLock().lock();
        boolean ok = criticalSectionWasAtomic();
        if (!ok) log(id+": CS was not atomic!");
        if (safe) lock.readLock().unlock();
      }
    });
    t.start();
    return t;
  }

  // Unsafe writer executes CS N times, without
  // holding a lock. This can cause CS to be
  // executed non-atomically which can be detected.
  //
  // Safe writer executes CS N times, while
  // holding a writer lock. This forces the
  // absence of other writers or any reader,
  // and allows CS to always be executed
  // atomically which can be verified.
  static Thread writer(String id, boolean safe) {
    Thread t = new Thread(() -> {
      for (int i=0; i<CS; i++) {
        if(safe) lock.writeLock().lock();
        criticalSection();
        if(safe) lock.writeLock().unlock();
      }
      log(id+": done");
    });
    t.start();
    return t;
  }

  // Tests to see if threads execute critical
  // section atomically.
  static void testThreads(boolean safe) {
    String type = safe? "safe" : "unsafe";
    log("Starting "+RD+" "+type+" readers ...");
    log("Starting "+WR+" "+type+" writers ...");
    Thread[] readers = new Thread[RD];
    Thread[] writers = new Thread[WR];
    for (int i=0; i<RD; i++) 
      readers[i] = reader("R"+i, safe);
    for (int i=0; i<WR; i++)
      writers[i] = writer("W"+i, safe);
    try {
    for (int i=0; i<RD; i++)
      readers[i].join();
    for (int i=0; i<WR; i++)
      writers[i].join();
    }
    catch(InterruptedException e) {}
    boolean atomic = criticalSectionWasAtomic();
    log("Critical Section was atomic? "+atomic);
    log("");
  }

  public static void main(String[] args) {
    lock = new SimpleReadWriteLock();
    sharedData = new double[SD];
    testThreads(false);
    sharedData = new double[SD];
    testThreads(true);
  }

  static void log(String x) {
    System.out.println(x);
  }
}
