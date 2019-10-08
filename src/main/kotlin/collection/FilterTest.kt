package collection

data class Dummy(val name: String)

fun main() {
    val list = mutableListOf<Dummy?>()
    list.add(Dummy("A"))
    list.add(null)
    println(list)
    val notNullList = list.filterNotNull()
    println(notNullList)



}
