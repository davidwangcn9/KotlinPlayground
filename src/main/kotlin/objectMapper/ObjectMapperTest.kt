package objectMapper

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature


//@JsonTypeInfo(
//    use = JsonTypeInfo.Id.NAME,
//    include = JsonTypeInfo.As.PROPERTY,
//    property = "type"
//)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Student::class, name = "student"),
    JsonSubTypes.Type(value = Teature::class, name = "teature")
)
abstract class People {
    lateinit var name: String
}

class Student : People() {
    var score: Int = 0

//    constructor(name: String, score: Int) : this() {
//        this.name = name
//        this.score = score
//    }
}

class Teature : People() {
    var salary: Long = 0

//    constructor(name: String, salary: Long) : this() {
//        this.name = name
//        this.salary = salary
//    }
}

fun main() {

    val student = Student().apply {
        name = "yang"
        score = 100
    }
    val teature = Teature().apply {
        name = "Li"
        salary = 3000
    }
    val peopleList = mutableListOf<People>(student, teature)
    val om = ObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, true)
    val str = om.writeValueAsString(peopleList)
    println(str)
    val objBack = om.readValue(str, Array<People>::class.java)
    println(objBack)
}
