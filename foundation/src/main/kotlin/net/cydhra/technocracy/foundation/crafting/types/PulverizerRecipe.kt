package net.cydhra.technocracy.foundation.crafting.types

import net.minecraft.item.crafting.Ingredient

/**
 * A recipe model type for pulverizer recipe with one input type, one output type and processing time cost
 *
 * @param input the input item stack for the pulverizer
 * @param output the output produced with given input
 * @param cost the base cost in ticks for the recipe to complete
 */
data class PulverizerRecipe(val input: Ingredient, val output: Ingredient, val cost: Int) : IRecipe