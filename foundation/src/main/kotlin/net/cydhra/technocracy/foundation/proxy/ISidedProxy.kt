package net.cydhra.technocracy.foundation.proxy

interface ISidedProxy {
    fun preInit()

    fun init()

    fun postInit()
}