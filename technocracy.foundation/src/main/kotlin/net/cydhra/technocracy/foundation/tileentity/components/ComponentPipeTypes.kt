package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.util.*


class ComponentPipeTypes : IComponent {

    var types = mutableSetOf<PipeType>()

    override fun serializeNBT(): NBTBase {
        val base = NBTTagCompound()
        base.setInteger("amount", types.size)
        types.forEachIndexed { index, type ->
            base.setString(index.toString(), type.name)
        }

        return base
    }

    override fun deserializeNBT(nbt: NBTBase) {
        val base = nbt as NBTTagCompound
        if (base.hasKey("amount")) {
            val amount = base.getInteger("amount")

            for (i in 0 until amount) {
                val name = base.getString("$i")
                val optional = Arrays.stream(PipeType.values()).filter { it.name == name }.findFirst()
                if (optional.isPresent)
                    types.add(optional.get())
            }
        }
    }
}