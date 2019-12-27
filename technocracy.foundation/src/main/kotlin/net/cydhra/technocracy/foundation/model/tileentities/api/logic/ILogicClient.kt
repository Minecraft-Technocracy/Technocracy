package net.cydhra.technocracy.foundation.model.tileentities.api.logic

/**
 * Entities that can adopt an [ILogic] strategy must implement this interface. Since the implementation is trivial,
 * adopting entities may use a delegate to implement it.
 */
interface ILogicClient {

    /**
     * Register a new logic strategy.
     *
     * @param strategy [ILogic] implementation
     * @param name a unique name for the strategy instance. no two instances with the same name may be registered at
     * a client
     */
    fun addLogicStrategy(strategy: ILogic, name: String)

    /**
     * Remove the logic strategy with the given name
     */
    fun removeLogicStrategy(name: String)

    /**
     * Updates all adopted logic strategies. Must be called by the implementor on its tick update function. Calls
     * update on adopted strategies on behalf of the implementation.
     */
    fun tick()
}