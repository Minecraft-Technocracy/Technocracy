package net.cydhra.technocracy.foundation.tileentity.logic

/**
 * Interface used by tile entities to implement recurring aspects of behaviour such as transforming items using recipes.
 */
interface ILogic {

    /**
     * Called before the logic client executes the main [processing] function. If any [ILogic] implementation of the
     * client returns ``false`` during this method, [processing] is not called at all.
     */
    fun preProcessing(): Boolean

    /**
     * Main processing. Only called if no [ILogic] implementation registered at the client returned ``false``.
     */
    fun processing()

    /**
     * Called after the [processing] function.
     *
     * @param wasProcessing if the [processing] executed successfully
     */
    fun postProcessing(wasProcessing: Boolean)
}