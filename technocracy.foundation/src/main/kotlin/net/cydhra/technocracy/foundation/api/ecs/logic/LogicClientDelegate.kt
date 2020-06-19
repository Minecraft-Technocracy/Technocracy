package net.cydhra.technocracy.foundation.api.ecs.logic

/**
 * Default implementation of an [ILogicClient] that just accepts registered [ILogic] implementations and updates them
 * on each [tick] call without further logic.
 */
class LogicClientDelegate<T : ILogicParameters> : ILogicClient<T> {

    private val logicStrategies: MutableMap<String, ILogic<T>> = mutableMapOf()

    override fun addLogicStrategy(strategy: ILogic<T>, name: String) {
        if (logicStrategies.containsKey(name)) {
            throw IllegalArgumentException("cannot add two logic strategies with the same name")
        }

        logicStrategies[name] = strategy
    }

    override fun removeLogicStrategy(name: String) {
        this.logicStrategies.remove(name)
    }

    override fun tick(logicParameters: T) {
        val canProcess = this.logicStrategies.values.all { it.preProcessing(logicParameters) }

        if (canProcess) {
            this.logicStrategies.values.forEach { it.processing(logicParameters) }
        }
        this.logicStrategies.values.forEach { it.postProcessing(canProcess, logicParameters) }
    }
}