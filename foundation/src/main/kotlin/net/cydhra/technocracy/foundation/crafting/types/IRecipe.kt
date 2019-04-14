package net.cydhra.technocracy.foundation.crafting.types

import net.minecraft.item.ItemStack

interface IRecipe {

    /**
     * Match a set of input stacks against the recipe. If the input stacks conform in type and size to the recipe,
     * true is returned.
     *
     * @param stacks all input stacks that shall be matched against the recipe
     *
     * @return if the recipe can be successfully processed from the input stacks
     */
    fun conforms(vararg stacks: List<ItemStack>): Boolean
}