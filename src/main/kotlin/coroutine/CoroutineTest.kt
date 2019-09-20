package coroutine

import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.Optional
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.Executors

val sets = ConcurrentSkipListSet<String>()

//fun main(args: Array<String>) {
//    runBlocking {
//        val start = System.currentTimeMillis()
//        println("主线程开始执行")
//        var jobs: MutableList<Job> = mutableListOf()
//
//
////        (1..10).forEach {
////            jobs.add(launch(newFixedThreadPoolContext(1, "mycontext")) {
////                runSomeThing(it)
////                println("打印第" + it + "次")
////            })
////        }
//
//        launch(newFixedThreadPoolContext(5, "mycontext")) {
//            (1..10).forEach {
//                launch{
//                    runSomeThing(it)
//                    println("打印第" + it + "次")
//                }
//            }
//        }
//
//
//        jobs.forEach { it.join() }
//        val takes = System.currentTimeMillis() - start
//        println("主线程结束执行, takes: $takes ms")
//        Thread.sleep(300L)
//        println("Threads: $sets ; size: ${sets.size}")
//    }
//}

class Data(val index: Int)


fun main(args: Array<String>) {

    val dataList: List<Data> = listOf()
    val available = dataList.none { it.index == 1 }
    println(available)

    val s1: Optional<Data> = Optional.empty()
    val s2: Optional<Data> = Optional.of(Data(2))
    val result1 = s1.map { it.index }.orElse(0)
    val result2 = s2.map { it.index }.orElse(0)
    println(result1)
    println(result2)

    val start = System.currentTimeMillis()

//    (1..11).map { Data(it) }.toList().batchLaunch(5) {
//        runData(it)
//    }
    (1..11).map { Data(it) }.toList().batchExecute(5) {
        runData(it)
    }

    Thread.sleep(300L)
    println("Threads: $sets ; size: ${sets.size}")
}

val batchSize: Int = 5
fun <T> Collection<T>.batchLaunch(parallelNumber: Int, action: suspend (T) -> Unit) {
    val collection = this
    runBlocking {
        launch(newFixedThreadPoolContext(parallelNumber, "mycontext")) {
            collection.forEach {
                launch { action(it) }
            }
        }
//        var currentBatchIndex = 0
//        var jobs: MutableList<Job> = mutableListOf()
//        for ((index, value) in collection.withIndex()) {
//            val current = index / batchSize
//            if (current != currentBatchIndex) {
//                jobs.forEach { it.join() }
//                currentBatchIndex = current
//                jobs.clear()
//            }
//            jobs.add(launch(newFixedThreadPoolContext(5, "mycontext")) { action(value) })
//        }
    }
}

fun <T> Collection<T>.batchExecute(parallelNumber: Int, action: (T) -> Unit) {
    val collection = this
    var service = Executors.newFixedThreadPool(parallelNumber)
    collection.forEach {
        service.execute(kotlinx.coroutines.Runnable { action(it) })
    }
}


fun request(index: Int) {
    var connection: HttpURLConnection? = null
    var reader: BufferedReader? = null
    var url = URL("http://localhost:8084/api/email/test")
    connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connectTimeout = 8000
    connection.readTimeout = 8000

    var inStream = connection.inputStream
    reader = BufferedReader(InputStreamReader(inStream))
    var response = StringBuilder()
    var allText = reader.use(BufferedReader::readText)
    response.append(allText)
    println("$index - Response: ${response.toString()}")
}

fun runData(taskIndex: Data): Int {
    var now = Instant.now()
    println("***$now Processing ${taskIndex.index} with thread: ${getCurrentThreadName()}")
    request(taskIndex.index)
//    Thread.sleep(1000)
//    delay(1000)
    now = Instant.now()
    println("---$now Complete ${taskIndex.index} with thread: ${getCurrentThreadName()}")
    return 1
}

fun runSomeThing(taskIndex: Int): Int {
    println("*** Processing $taskIndex with thread: ${getCurrentThreadName()}")
//    delay(1000)
    request(taskIndex)
//    Thread.sleep(1000)
    println("--- Complete $taskIndex with thread: ${getCurrentThreadName()}")
    return 1
}

fun getCurrentThreadName(): String {
    val thread = Thread.currentThread()
    val key = "{ id: ${thread.id} name: ${thread.name}}"
    if (!sets.contains(key)) {
        sets.add(key)
    }
    return "Thread $key"
}
