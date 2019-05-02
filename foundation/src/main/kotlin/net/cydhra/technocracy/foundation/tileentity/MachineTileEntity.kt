package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate

open class MachineTileEntity : TCMachineTileEntity, ILogicClient by LogicClientDelegate() {
    override fun update() {
        TODO("not implemented")
    }
}