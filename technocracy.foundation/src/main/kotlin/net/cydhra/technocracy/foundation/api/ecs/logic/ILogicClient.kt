package net.cydhra.technocracy.foundation.api.ecs.logic

/**
 * Entities that can adopt an [ILogic] strategy must implement this interface. Since the implementation is trivial,
 * adopting entities may use a delegate to implement it.
 *
 * @param T additional ILogicParameters dependent on the client implementation. Since this system is used for
 * item stacks as well as tile entities, different information are available to the client implementation through its
 * instance. Additional information must be provided from outside using a parameter in [tick]. For example, the logic
 * client for item stacks cannot hold the player using the item stack, because the item stack could potentially
 * switch user. Therefore, the player must be provided through [T]
 */
interface ILogicClient<T : ILogicParameters> {

    /**
     * Register a new logic strategy.
     *
     * @param strategy [ILogic] implementation
     * @param name a unique name for the strategy instance. no two instances with the same name may be registered at
     * a client
     */
    fun addLogicStrategy(strategy: ILogic<T>, name: String)

    /**
     * Remove the logic strategy with the given name
     */
    fun removeLogicStrategy(name: String)

    /**
     * Updates all adopted logic strategies. Must be called by the implementor on its tick update function. Calls
     * update on adopted strategies on behalf of the implementation.
     *
     * @param logicParameters additional parameters required for client implementations.
     *
     * @see ILogicClient
     */
    fun tick(logicParameters: T)
}