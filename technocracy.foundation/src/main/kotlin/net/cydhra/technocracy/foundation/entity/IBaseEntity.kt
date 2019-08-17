package net.cydhra.technocracy.foundation.entity


interface IBaseEntity {

    val primaryColor: Int
    val secondaryColor: Int

    fun getName(): String
}