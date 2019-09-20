package lazy


fun main() {
    println("Demo to show how to zip files under defined structure")

    val demo = Demo()
    demo.testLazy()
    demo.testLazy2()
}

class Demo {

    fun callOnce(): Boolean {
        println("********** only call once ************")
        return true
    }

    private val available by lazy {
        callOnce()
    }

    fun testLazy() {
        if (available) {
            println("********1*********")
        }
    }

    fun testLazy2() {
        if (available) {
            println("********2*********")
        }
    }
}
