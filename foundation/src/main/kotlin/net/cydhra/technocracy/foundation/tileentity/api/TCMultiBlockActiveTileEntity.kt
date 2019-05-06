package net.cydhra.technocracy.foundation.tileentity.api

interface TCMultiBlockActiveTileEntity {

    /**
     * @return true, if the multiblock structure is valid, false if it is incomplete or otherwise invalid.
     */
    fun validateStructure(): Boolean
}