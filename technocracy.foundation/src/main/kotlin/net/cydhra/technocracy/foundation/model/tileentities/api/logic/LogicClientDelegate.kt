package net.cydhra.technocracy.foundation.model.tileentities.api.logic

/**
 * Default implementation of an [ILogicClient] that just accepts registered [ILogic] implementations and updates them
 * on each [tick] call without further logic.
 */
class LogicClientDelegate : ILogicClient {

    private val logicStrategies: MutableMap<String, ILogic> = mutableMapOf()

    override fun addLogicStrategy(strategy: ILogic, name: String) {
        logicStrategies[name] = strategy
    }

    override fun removeLogicStrategy(name: String) {
        this.logicStrategies.remove(name)
    }

    override fun tick() {
        val canProcess = this.logicStrategies.values.all(ILogic::preProcessing)

        if (canProcess) {
            this.logicStrategies.values.forEach(ILogic::processing)
        }
        this.logicStrategies.values.forEach { it.postProcessing(canProcess) }
    }
}