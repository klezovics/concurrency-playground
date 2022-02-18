import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConcurrencyTest {

    @Test
    fun testCanRunTwoThreads() {
        val t1 = Thread {
            for (ii in 0 until 10) println("T1: $ii")
        }

        val t2 = Thread {
            for (ii in 0 until 10) println("T2: $ii")
        }

        println("Start")
        t1.start()
        t2.start()
        t1.join()
        t2.join()
        println("Finish")
    }

    @Test
    fun testNotThreadSafeStuff() {
        val c1 = SimpleCounter()
        val iterCount = 1000 * 1000 * 1000

        val it1 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val it2 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val start = System.currentTimeMillis()
        it1.start()
        it2.start()
        it1.join()
        it2.join()
        val end = System.currentTimeMillis()

        println("Counter is ${c1.get()}")
        println("Time is ${end - start} ms")
    }

    @Test
    fun testSynchronizedCounter() {
        val c1 = SyncCounter()
        val iterCount = 1000 * 1000 * 1000

        val it1 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val it2 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val start = System.currentTimeMillis()
        it1.start()
        it2.start()
        it1.join()
        it2.join()
        val end = System.currentTimeMillis()

        println("Counter is ${c1.get()}")
        println("Time is ${(end - start)} ms")
    }

    @Test
    fun testAtomicCounter() {
        val c1 = AtomicCounter()
        val iterCount = 1000 * 1000 * 1000

        val it1 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val it2 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val start = System.currentTimeMillis()
        it1.start()
        it2.start()
        it1.join()
        it2.join()
        val end = System.currentTimeMillis()

        println("Counter is ${c1.get()}")
        println("Time is ${(end - start)} ms")
    }

    @Test
    fun testSyncInnerCounter() {
        val c1 = SyncInnerCounter()
        val iterCount = 1000 * 1000 * 1000

        val it1 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val it2 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val start = System.currentTimeMillis()
        it1.start()
        it2.start()
        it1.join()
        it2.join()
        val end = System.currentTimeMillis()

        println("Counter is ${c1.get()}")
        println("Time is ${(end - start)} ms")
    }

    @Test
    fun testLockCounter() {
        val c1 = LockCounter()
        val iterCount = 1000 * 1000 * 1000

        val it1 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val it2 = IncrementerThread(c1) {
            for (ii in 0 until iterCount) c1.inc()
        }

        val start = System.currentTimeMillis()
        it1.start()
        it2.start()
        it1.join()
        it2.join()
        val end = System.currentTimeMillis()

        println("Counter is ${c1.get()}")
        println("Time is ${(end - start)} ms")
    }

    class IncrementerThread(val counter: Counter, val iterCount: Int = 1000 * 1000, r: Runnable) : Thread(r)

    interface Counter {
        fun inc()
        fun get(): Int
    }

    class AtomicCounter : Counter {
        var counter = AtomicInteger(0)
        override fun inc() {
            counter.incrementAndGet()
        }

        override fun get(): Int {
            return counter.get()
        }
    }

    class SimpleCounter : Counter {
        var counter = 0
        override fun inc() {
            counter++
        }

        override fun get() = counter
    }

    class SyncCounter : Counter {
        var counter = 0

        @Synchronized
        override fun inc() {
            counter++
        }

        override fun get(): Int {
            return counter
        }
    }

    class SyncInnerCounter : Counter {
        var counter = 0

        override fun inc() {
            synchronized(this) {
                counter++
            }
        }

        override fun get(): Int {
            return counter
        }
    }

    class LockCounter : Counter {
        var counter = 0
        var lock: Lock = ReentrantLock()

        override fun inc() {
            lock.lock()
            try {
                counter++
            } finally {
                lock.unlock()
            }
        }

        override fun get(): Int {
            return counter
        }
    }
}
