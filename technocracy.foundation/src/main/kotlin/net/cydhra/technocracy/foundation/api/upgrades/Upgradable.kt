package net.cydhra.technocracy.foundation.api.upgrades

interface Upgradable {

    /**
     * @return true, if this upgradable entity supports the given parameter, false if not
     */
    fun supportsParameter(parameter: UpgradeParameter): Boolean

    /**
     * Modify the given upgrade parameter by the given amount.
     *
     * @param parameter upgrade parameter to modify
     * @param modification modifier to add to the parameter
     */
    fun upgradeParameter(parameter: UpgradeParameter, modification: Double)
}