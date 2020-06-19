package net.cydhra.technocracy.foundation.api.upgrades


interface Installable {
    /**
     * @return true, if this upgradable entity supports the given parameter, false if not
     */
    fun supportsInstallParameter(parameter: UpgradeParameter): Boolean

    /**
     * Modify the given upgrade parameter by the given amount.
     *
     * @param parameter upgrade parameter to modify
     * @param attach attach or not
     */
    fun installParameter(parameter: UpgradeParameter, attach: Boolean)
}