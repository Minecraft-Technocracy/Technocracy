# Technocracy
[![Build Status](https://jenkins.flaflo.xyz/view/Technocracy/job/Technocracy/badge/icon)](https://jenkins.flaflo.xyz/view/Technocracy/job/Technocracy/)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)

Yet another absolutely excessive tech mod for minecraft.
A better description shall be added soon...


# Development Setup
- Clone the repository
- Every module must be set up with [ForgeGradle](https://github.com/MinecraftForge/ForgeGradle).
However, by running `gradle :setup` within project root, all necessary setup steps are executed automatically.
- You can run foundation with `gradle technocracy.foundation:runClient`.
You can replace `technocracy.foundation` with any other module name.
Note, that ForgeGradle seems to have problems with mods depending on modules with access-transformers.
Therefore, the monolithic build currently does not work.
- If you use IntelliJ, you might have to hit `Reimport All Gradle Projects` within your gradle-tool-window after Step 2.
But there are working run-configurations for all modules present in IntelliJ.

# Troubleshooting
- **Java 8**:
Forge does not support modern JDK versions.
You will need a JDK 8 in order to contribute. 
Try the version by Amazon ([Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)) 
 if you cannot find another JDK 8, or use Open-JDK.
- **Duplicate Mods**:
If, while started from IntelliJ, a Minecraft instance complains about duplicate mods being present,
make sure that in `Settings>Build, Execution, Deployment>Build Tools>Gradle` the setting `Build and run using` is set
 to `IntelliJ IDEA` instead of `Gradle`.
- **Missing assets**:
If, while started from IntelliJ, a Minecraft instance misses all its assets (including textures of added blocks/items, language file, etc), try the same thing as in **Duplicate Mods**
