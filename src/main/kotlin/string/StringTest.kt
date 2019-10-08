package string

fun main() {
    val test = "abc/1/wang"
    val result = test.split("/").drop(1).first()
    print(result)

}
