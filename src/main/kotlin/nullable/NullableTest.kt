package nullable

data class Dog(
    val name: String?,
    val tag: String?
)

fun main() {
    println(createDog(null,null))
    println(createDog("gugu",null))
    println(createDog(null,"bark"))
    println(createDog("baozi","bark"))
}

fun createDog(name: String?, tag: String?): Dog? = name?.let { tag?.let { Dog(name, tag) } }
