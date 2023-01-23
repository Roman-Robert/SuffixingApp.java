import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuffixingApp {

    private static final Logger logger = Logger.getLogger(SuffixingApp.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.log(Level.SEVERE, "Config file is empty");
        } else {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(args[0]))) {
                Properties properties = new Properties();
                properties.load(reader);

                String mode = properties.getProperty("mode");

                if (!mode.equalsIgnoreCase("copy") || !mode.equalsIgnoreCase("move")) {
                    logger.log(Level.SEVERE, "Mode is not recognized: " + mode);
                }

                String suffix = properties.getProperty("suffix");

                if (suffix == null || suffix.isEmpty()) {
                    logger.log(Level.SEVERE, "No suffix is configured");
                }

                String files = properties.getProperty("files");

                if (files == null || files.isEmpty()) {
                    logger.log(Level.WARNING, "No files are configured to be copied/moved");
                }

                String[] filesPaths = files.split(":");

                if ("copy".equalsIgnoreCase(mode)) {
                    copyFiles(filesPaths, suffix);
                }
                if ("move".equalsIgnoreCase(mode)) {
                    moveFiles(filesPaths, suffix);
                }
            } catch (Exception e) {
                logger.log(Level.INFO, "Exception");
            }
        }
    }

    //Метод добавляет суффикс перед точкой в имени файла
    public static String addSuffix(String fileName, String suffix) {
        int index = fileName.indexOf(".");
        return fileName.substring(0, index) + suffix + fileName.substring(index);
    }

    //Метод копирует файл
    public static void copyFiles(String[] paths, String suffix) {
        for (String path : paths) {
            if (Files.exists(Path.of(path))) {
                String newPath = addSuffix(path, suffix);
                try {
                    Files.copy(Path.of(path), Path.of(newPath));
                    logger.log(Level.INFO, path + " -> " + newPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.log(Level.SEVERE, "No such file: " + path.replaceAll("\\\\", "/"));
            }
        }
    }

    //Метод перемещает файл
    public static void moveFiles(String[] paths, String suffix) {
        for (String path : paths) {
            if (Files.exists(Path.of(path))) {
                String newPath = addSuffix(path, suffix);
                try {
                    Files.move(Path.of(path), Path.of(newPath));
                    logger.log(Level.INFO, path + " => " + newPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.log(Level.SEVERE, "No such file: " + path.replaceAll("\\\\", "/"));
            }
        }
    }
}
