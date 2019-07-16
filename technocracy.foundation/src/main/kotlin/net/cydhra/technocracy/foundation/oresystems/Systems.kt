package net.cydhra.technocracy.foundation.oresystems

import net.cydhra.technocracy.foundation.oresystems.OreSystemBuilder.IntermediateProductType.GEAR
import net.cydhra.technocracy.foundation.oresystems.OreSystemBuilder.IntermediateProductType.SHEET
import net.minecraft.init.Blocks
import net.minecraft.init.Items

val aluminumSystem = oreSystem {
    name = "aluminum"
    color = 0xFFFFFF

    create(GEAR, SHEET)

    generate {
        veinsPerChunk = 14
        amountPerVein = 6
        minHeight = 10
        maxHeight = 45
    }
}

val copperSystem = oreSystem {
    name = "copper"
    color = 0xF78725

    create(GEAR, SHEET)

    generate {
        veinsPerChunk = 20
        amountPerVein = 10
        minHeight = 30
        maxHeight = 65
    }
}

val leadSystem = oreSystem {
    name = "lead"
    color = 0x9696BE

    create(SHEET)

    generate {
        veinsPerChunk = 18
        amountPerVein = 10
        minHeight = 10
        maxHeight = 65
    }
}

val lithiumSystem = oreSystem {
    name = "lithium"
    color = 0x176B59

    generate {
        veinsPerChunk = 8
        amountPerVein = 7
        minHeight = 1
        maxHeight = 60
    }
}

val nickelSystem = oreSystem {
    name = "nickel"
    color = 0xB56932

    generate {
        veinsPerChunk = 8
        amountPerVein = 4
        minHeight = 1
        maxHeight = 20
    }
}

val niobiumSystem = oreSystem {
    name = "niobium"
    color = 0x4682B4

    generate {
        veinsPerChunk = 12
        amountPerVein = 6
        minHeight = 1
        maxHeight = 30
    }
}

val silverSystem = oreSystem {
    name = "silver"
    color = 0xE0E0FF

    generate {
        veinsPerChunk = 8
        amountPerVein = 5
        minHeight = 1
        maxHeight = 20
    }
}

val tinSystem = oreSystem {
    name = "tin"
    color = 0xC6C6C6

    generate {
        veinsPerChunk = 18
        amountPerVein = 8
        minHeight = 25
        maxHeight = 60
    }
}

val ironSystem = oreSystem {
    name = "iron"
    color = 0xCAFEBAB

    importOre(Blocks.IRON_ORE)
    importIngot(Items.IRON_INGOT)

    create(GEAR, SHEET)
}

val goldSystem = oreSystem {
    name = "gold"
    color = 0xCAFEBAB

    importOre(Blocks.GOLD_ORE)
    importIngot(Items.GOLD_INGOT)
}