package lazy

class IamLazyMan {
    lateinit var name: String
    lateinit var title: String
}

fun main() {
    val man = IamLazyMan().apply { name = "david" }
    println(man.name)
    man.title = "bad"
    println(man.title)

}
