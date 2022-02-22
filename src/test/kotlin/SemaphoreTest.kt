import org.junit.jupiter.api.Test
import java.util.concurrent.Semaphore

// A semaphore allows to control access to a pool of N items
// See example here https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html
// Its a somewhat more advanced version of a binary lock
class SemaphoreTest {

    @Test
    fun testSemaphore() {
        val s = Semaphore(1)
        val counter = Counter()
        val inc = Thread(Incrementor("A", counter, s))
        val dec = Thread(Decrementor("B", counter, s))

        inc.start()
        dec.start()
    }

    class Decrementor(val tname: String, val counter: Counter, val s: Semaphore) : Runnable {
        override fun run() {

            try {
                println("$tname: waiting for permit")
                s.acquire()
                for (ii in 0 until 5) {
                    counter.count -= 1
                    println("$tname: count is ${counter.count}")
                    // sleep(500)
                }
                println("$tname: done")
                s.release()
                // yield()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    class Incrementor(val tname: String, val counter: Counter, val s: Semaphore) : Runnable {
        override fun run() {
            try {
                println("$tname: waiting for permit")
                s.acquire()
                for (ii in 0 until 5) {
                    counter.count += 1
                    println("$tname: count is ${counter.count}")
                    // sleep(500)
                }
                println("$tname: done")
                s.release()
                // yield()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    class Counter {
        var count: Int = 0
    }
}
