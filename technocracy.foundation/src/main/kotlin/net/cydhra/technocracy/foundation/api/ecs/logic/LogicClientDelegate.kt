package net.cydhra.technocracy.foundation.api.ecs.logic

/**
 * Default implementation of an [ILogicClient] that just accepts registered [ILogic] implementations and updates them
 * on each [tick] call without further logic.
 */
class LogicClientDelegate : ILogicClient {

    private val logicStrategies: MutableMap<String, ILogic> = mutableMapOf()

    override fun addLogicStrategy(strategy: ILogic, name: String) {
        if (logicStrategies.containsKey(name)) {
            throw IllegalArgumentException("cannot add two logic strategies with the same name")
        }

        logicStrategies[name] = strategy
    }

    override fun removeLogicStrategy(name: String) {
        this.logicStrategies.remove(name)
    }

    override fun tick(logicStack: ILogicParameters) {
        val canProcess = this.logicStrategies.values.all(ILogic::preProcessing)

        if (canProcess) {
            this.logicStrategies.values.forEach(ILogic::processing)
        }
        this.logicStrategies.values.forEach { it.postProcessing(canProcess) }
    }
}