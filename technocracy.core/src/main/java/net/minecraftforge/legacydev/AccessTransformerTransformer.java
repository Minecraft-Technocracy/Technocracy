package net.minecraftforge.legacydev;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AccessTransformerTransformer implements IClassTransformer {

    private static final String MOD_ATD_CLASS = "net.minecraftforge.fml.common.asm.transformers.ModAccessTransformer";

    public AccessTransformerTransformer() {
        doStuff(AccessTransformerTweaker.loader);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doStuff(LaunchClassLoader classloader) {
        // the class and instance of ModAccessTransformer
        Class<? extends IClassTransformer> clazz = null;
        IClassTransformer instance = null;

        // find the instance I want. AND grab the type too, since thats better than Class.forName()
        for (IClassTransformer transformer : classloader.getTransformers()) {
            if (transformer.getClass().getCanonicalName().endsWith(MOD_ATD_CLASS)) {
                clazz = transformer.getClass();
                instance = transformer;
            }
        }

        // impossible! but i will ignore it.
        if (clazz == null || instance == null) {
            Main.LOGGER.severe( "ModAccessTransformer was somehow not found.");
            return;
        }

        // grab the list of Modifiers I wanna mess with
        Collection<Object> modifiers = null;
        try {
            // super class of ModAccessTransformer is AccessTransformer
            Field f = clazz.getSuperclass().getDeclaredFields()[1]; // its the modifiers map. Only non-static field there.
            f.setAccessible(true);

            modifiers = ((com.google.common.collect.Multimap) f.get(instance)).values();
        } catch (Throwable t) {
            Main.LOGGER.severe("AccessTransformer.modifiers field was somehow not found...");
            return;
        }

        if (modifiers.isEmpty()) {
            return; // hell no am I gonna do stuff if its empty..
        }

        // grab the field I wanna hack
        Field nameField = null;
        try {
            // get 1 from the collection
            Object mod = null;
            for (Object val : modifiers) {
                mod = val;
                break;
            } // i wish this was cleaner

            nameField = mod.getClass().getFields()[0]; // first field. "name"
            nameField.setAccessible(true); // its alreadypublic, but just in case
        } catch (Throwable t) {
            Main.LOGGER.severe("AccessTransformer.Modifier.name field was somehow not found...");
            return;
        }

        // read the field and method CSV files.
        Map<String, String> nameMap = Maps.newHashMap();
        try {
            readCsv(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("fields.csv"))).lines(), nameMap);
            readCsv(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("methods.csv"))).lines(), nameMap);
        } catch (Exception e) {
            // If I cant find these.. something is terribly wrong.
            e.printStackTrace();
            return;
        }

        // finally hit the modifiers
        for (Object modifier : modifiers) // these are instances of AccessTransformer.Modifier
        {
            String name;
            try {
                name = (String) nameField.get(modifier);
                String newName = nameMap.get(name);
                if (newName != null) {
                    nameField.set(modifier, newName);
                }
            } catch (Exception e) {
                // impossible. It would have failed earlier if possible.
            }
        }
    }

    private void readCsv(Stream<String> file, Map<String, String> map) throws IOException
    {
        Splitter split = Splitter.on(',').trimResults().limit(3);
        for (String line : file.collect(Collectors.toList()))
        {
            if (line.startsWith("searge")) // header line
                continue;

            List<String> splits = split.splitToList(line);
            map.put(splits.get(0), splits.get(1));
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return basicClass; // nothing here
    }
}
