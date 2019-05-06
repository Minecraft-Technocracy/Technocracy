package net.cydhra.technocracy.foundation.tileentity.logic

/**
 * Interface used by tile entities to implement recurring aspects of behaviour such as transforming items using recipes.
 */
interface ILogic {

    /**
     * Update function for the logic component. Called each tick by the tile entity
     */
    fun update()
}