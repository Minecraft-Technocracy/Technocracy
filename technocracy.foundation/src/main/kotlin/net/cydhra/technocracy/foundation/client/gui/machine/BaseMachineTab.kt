package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.util.ResourceLocation

abstract class BaseMachineTab(val machine: MachineTileEntity, parent: TCGui, icon: ResourceLocation) : TCTab(name = machine.blockType?.localizedName ?: "", parent = parent, icon = icon)
                                                                                                                            // its null if the te is not placed in the world (required for jei)
