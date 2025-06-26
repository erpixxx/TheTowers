package dev.erpix.thetowers.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Path;
import java.util.Set;

/**
 * Utility class for loading and saving YAML configuration files.
 */
public final class YamlLoader {

    /**
     * Loads a YAML file from the given path and converts kebab-case keys to camelCase.
     *
     * @param <T> the type of the object to load.
     * @param configPath the path to the YAML file.
     * @param type the target class type for mapping.
     * @return an instance of T loaded from the YAML file.
     * @throws IOException if file reading fails.
     */
    public static <T> T load(Path configPath, Class<T> type) throws IOException {
        try (Reader reader = new FileReader(configPath.toFile())) {
            Constructor constructor = new Constructor(type, new LoaderOptions());
            constructor.setPropertyUtils(new KebabCasePropertyUtils());
            Yaml yaml = new Yaml(constructor);
            return yaml.loadAs(reader, type);
        }
    }

    /**
     * Saves an object to a YAML file at the specified path.
     *
     * @param configPath the path where the YAML file will be saved.
     * @param object the object to save.
     * @throws IOException if file writing fails.
     */
    public static void save(Path configPath, Object object) throws IOException {
        try (Writer writer = new FileWriter(configPath.toFile())) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            Representer representer = new MapRepresenter(options);
            representer.setPropertyUtils(new KebabCasePropertyUtils());
            Yaml yaml = new Yaml(representer);
            yaml.dump(object, writer);
        }
    }

    /**
     * Here only because SnakeYAML have to tag every JavaBeans with !!class...
     * This overrides it and forces it to act like a normal serializer.
     */
    private static class MapRepresenter extends Representer {

        public MapRepresenter(DumperOptions options) {
            super(options);
        }

        @Override
        protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
            addClassTag(javaBean.getClass(), Tag.MAP);
            return super.representJavaBean(properties, javaBean);
        }

    }

    /**
     * Custom PropertyUtils that converts kebab-case keys to camelCase.
     */
    private static class KebabCasePropertyUtils extends PropertyUtils {

        @Override
        public Property getProperty(Class<?> type, String name) {
            try {
                return super.getProperty(type, name);
            } catch (Exception e) {
                return super.getProperty(type, kebabToCamelCase(name));
            }
        }

        private String kebabToCamelCase(String name) {
            String[] parts = name.split("-");
            if (parts.length == 0) return name;

            StringBuilder sb = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    sb.append(Character.toUpperCase(parts[i].charAt(0)))
                            .append(parts[i].substring(1));
                }
            }
            return sb.toString();
        }

    }

}
