package collection

fun main() {
    println(getName())
}

fun getName(): String = "Abc".also { println("I am going to return") }
