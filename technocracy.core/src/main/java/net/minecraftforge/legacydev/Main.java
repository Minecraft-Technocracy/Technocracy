/*
 * LegacyDev
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.legacydev;

import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    static Logger LOGGER = setupLogger();

    private static Logger setupLogger() {
        InputStream is = Main.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Logger.getLogger("LegacyDev");
    }

    void setupLegacyGradleEnvs() {

        String home = System.getProperty("user.home");

        String mcp = getenv("MCP_MAPPINGS");

        //snapshot_20180609-1.12
        String type = "mcp_" + mcp.split("_")[0];
        String[] tmp = mcp.split("_")[1].split("-");
        String id = tmp[0];
        String version = getenv("MC_VERSION");

        final File SRG_DIR = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/srgs");
        final File SRG_NOTCH_SRG = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/" + version + "/srgs/notch-srg.srg");
        final File SRG_NOTCH_MCP = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/" + version + "/srgs/notch-mcp.srg");
        final File SRG_SRG_MCP = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/" + version + "/srgs/srg-mcp.srg");
        final File SRG_MCP_SRG = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/" + version + "/srgs/mcp-srg.srg");
        final File SRG_MCP_NOTCH = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "/" + version + "/srgs/mcp-notch.srg");
        final File CSV_DIR = new File(home + "/.gradle/caches/minecraft/de/oceanlabs/mcp/" + type + "/" + id + "");

        try {
            System.setProperty("net.minecraftforge.gradle.GradleStart.srgDir", SRG_DIR.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.notch-srg", SRG_NOTCH_SRG.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.notch-mcp", SRG_NOTCH_MCP.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.srg-mcp", SRG_SRG_MCP.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.mcp-srg", SRG_MCP_SRG.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.mcp-notch", SRG_MCP_NOTCH.getCanonicalPath());
            System.setProperty("net.minecraftforge.gradle.GradleStart.csvDir", CSV_DIR.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void start(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String natives = getenv("nativesDirectory");
        if (natives != null) {
            LOGGER.info("Natives: " + natives);
            handleNatives(natives);
        }

        String mainClass = getenv("mainClass");
        if (mainClass == null)
            throw new IllegalArgumentException("Must specify mainClass environment variable");
        LOGGER.info("Main Class: " + mainClass);


        setupLegacyGradleEnvs();
        String srg2mcp = getenv("MCP_TO_SRG");
        if (srg2mcp != null) {
            LOGGER.info("Srg2Mcp: " + srg2mcp);
            System.setProperty("net.minecraftforge.gradle.GradleStart.srg.srg-mcp", srg2mcp);
        }

        String[] cleanArgs = parseArgs(args);

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int x = 0; x < cleanArgs.length; x++) {
            b.append(cleanArgs[x]);
            if ("--accessToken".equalsIgnoreCase(cleanArgs[x])) {
                b.append(", {REDACTED}");
                x++;
            }

            if (x < cleanArgs.length - 1)
                b.append(", ");
        }
        b.append(']');
        LOGGER.info("Running with arguments: " + b.toString());

        GradleForgeHacks.searchCoremods(this);

        Class<?> cls = Class.forName(mainClass);
        Method main = cls.getDeclaredMethod("main", String[].class);
        main.invoke(null, new Object[]{cleanArgs});
    }

    protected void handleNatives(String path) {
    }

    protected Map<String, String> getDefaultArguments() {
        return new LinkedHashMap<>();
    }

    private String[] parseArgs(String[] args) {
        final Map<String, String> defaults = getDefaultArguments();

        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        for (String key : defaults.keySet())
            parser.accepts(key).withRequiredArg().ofType(String.class);

        final NonOptionArgumentSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        for (String key : defaults.keySet()) {
            if (options.hasArgument(key)) {
                String value = (String) options.valueOf(key);
                defaults.put(key, value);
                if (!"password".equalsIgnoreCase(key) && !"accessToken".equalsIgnoreCase(key))
                    LOGGER.info(key + ": " + value);
            }
        }

        List<String> extras = new ArrayList<>(nonOption.values(options));
        LOGGER.info("Extra: " + extras);

        List<String> lst = new ArrayList<>();
        defaults.forEach((k, v) -> {
            if (!nullOrEmpty(v)) {
                lst.add("--" + k);
                lst.add(v);
            }
        });

        String tweak = getenv("tweakClass");
        if (!nullOrEmpty(tweak)) {
            lst.add("--tweakClass");
            lst.add(tweak);

            lst.add("--tweakClass");
            lst.add("net.minecraftforge.legacydev.CoremodTweaker");
        }

        lst.addAll(extras);

        return lst.toArray(new String[lst.size()]);
    }

    protected String getenv(String name) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? null : value;
    }

    protected boolean nullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
