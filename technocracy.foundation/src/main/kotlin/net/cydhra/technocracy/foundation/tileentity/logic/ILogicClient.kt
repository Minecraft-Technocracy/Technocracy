package net.cydhra.technocracy.foundation.tileentity.logic

/**
 * Entities that can adopt an [ILogic] strategy must implement this interface. Since the implementation is trivial,
 * adopting entities may use a delegate to implement it.
 */
interface ILogicClient {

    /**
     * Register a new logic strategy.
     *
     * @param strategy [ILogic] implementation
     */
    fun addLogicStrategy(strategy: ILogic)

    /**
     * Updates all adopted logic strategies. Must be called by the implementor on its tick update function. Calls
     * update on adopted strategies on behalf of the implementation.
     */
    fun tick()
}