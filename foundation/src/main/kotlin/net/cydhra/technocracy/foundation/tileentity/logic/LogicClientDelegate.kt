package net.cydhra.technocracy.foundation.tileentity.logic

/**
 * Default implementation of an [ILogicClient] that just accepts registered [ILogic] implementations and updates them
 * on each [tick] call without further logic.
 */
class LogicClientDelegate : ILogicClient {

    private val logicStrategies: MutableSet<ILogic> = mutableSetOf()

    override fun addLogicStrategy(strategy: ILogic) {
        logicStrategies += strategy
    }

    override fun tick() {
        this.logicStrategies.forEach(ILogic::update)
    }
}