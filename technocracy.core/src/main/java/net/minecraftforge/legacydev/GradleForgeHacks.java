package net.minecraftforge.legacydev;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class GradleForgeHacks {

    // coremod hack
    private static final String COREMOD_VAR = "fml.coreMods.load";
    private static final String COREMOD_MF = "FMLCorePlugin";
    // AT hack
    private static final String MOD_ATD_CLASS = "net.minecraftforge.fml.common.asm.transformers.ModAccessTransformer";
    private static final String MOD_AT_METHOD = "addJar";

    public static final Map<String, File> coreMap = Maps.newHashMap();

    public static void searchCoremods(Main common) {
        // check for argument
        // initialize AT hack Method
        AtRegistrar atRegistrar = new AtRegistrar();

        URLClassLoader urlClassLoader = (URLClassLoader) Main.class.getClassLoader();
        for (URL url : urlClassLoader.getURLs()) {
            try {
                searchCoremodAtUrl(url, atRegistrar);
            } catch (IOException | InvocationTargetException | IllegalAccessException | URISyntaxException e) {
                Main.LOGGER.warning("GradleForgeHacks failed to search for coremod at url " + url);
                e.printStackTrace();
            }
        }

        // set property.
        Set<String> coremodsSet = Sets.newHashSet();
        if (!Strings.isNullOrEmpty(System.getProperty(COREMOD_VAR)))
            coremodsSet.addAll(Splitter.on(',').splitToList(System.getProperty(COREMOD_VAR)));
        coremodsSet.addAll(coreMap.keySet());
        System.setProperty(COREMOD_VAR, Joiner.on(',').join(coremodsSet));

    }

    private static void searchCoremodAtUrl(URL url, AtRegistrar atRegistrar) throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {
        if (!url.getProtocol().startsWith("file")) // because file urls start with file://
            return; //         this isn't a file

        File coreMod = new File(url.toURI().getPath());
        Manifest manifest = null;

        if (!coreMod.exists())
            return;

        if (coreMod.isDirectory()) {
            File manifestMF = new File(coreMod, "META-INF/MANIFEST.MF");
            if (manifestMF.exists()) {
                FileInputStream stream = new FileInputStream(manifestMF);
                manifest = new Manifest(stream);
                stream.close();
            }
        } else if (coreMod.getName().endsWith("jar")) // its a jar
        {
            try (JarFile jar = new JarFile(coreMod)) {
                manifest = jar.getManifest();
                if (manifest != null) {
                    atRegistrar.addJar(jar, manifest);
                }
            }
        }

        // we got the manifest? use it.
        if (manifest != null) {
            String clazz = manifest.getMainAttributes().getValue(COREMOD_MF);
            if (!Strings.isNullOrEmpty(clazz)) {
                Main.LOGGER.info("Found and added coremod: " + clazz);
                coreMap.put(clazz, coreMod);
            }
        }
    }

    /**
     * Hack to register jar ATs with Minecraft Forge
     */
    private static final class AtRegistrar {
        private static final Attributes.Name FMLAT = new Attributes.Name("FMLAT");

        @Nullable
        private Method newMethod = null;
        @Nullable
        private Method oldMethod = null;

        private AtRegistrar() {
            try {
                Class<?> modAtdClass = Class.forName(MOD_ATD_CLASS);
                try {
                    newMethod = modAtdClass.getDeclaredMethod(MOD_AT_METHOD, JarFile.class, String.class);
                } catch (NoSuchMethodException | SecurityException ignored) {
                    try {
                        oldMethod = modAtdClass.getDeclaredMethod(MOD_AT_METHOD, JarFile.class);
                    } catch (NoSuchMethodException | SecurityException ignored2) {
                        Main.LOGGER.severe("Failed to find method " + MOD_ATD_CLASS + "." + MOD_AT_METHOD);
                    }
                }
            } catch (ClassNotFoundException e) {
                Main.LOGGER.severe("Failed to find class " + MOD_ATD_CLASS);
            }
        }

        public void addJar(JarFile jarFile, Manifest manifest) throws InvocationTargetException, IllegalAccessException {
            if (newMethod != null) {
                String ats = manifest.getMainAttributes().getValue(FMLAT);
                if (ats != null && !ats.isEmpty()) {
                    newMethod.invoke(null, jarFile, ats);
                }
            } else if (oldMethod != null) {
                oldMethod.invoke(null, jarFile);
            }
        }
    }

    /* ----------- CUSTOM TWEAKER FOR COREMOD HACK --------- */

    // here and not in the tweaker package because classloader hell

}
