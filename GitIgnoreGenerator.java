import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class GitIgnoreGenerator {
    public static void create(String projectPath) throws IOException {
        File gitignoreFile = new File(projectPath + "/.gitignore");

        try (PrintWriter writer = new PrintWriter(gitignoreFile)) {
            writer.println("*.class");
            writer.println("*.log");
            writer.println(".DS_Store");
            writer.println("target/");
            writer.println(".idea/");
            writer.println("node_modules/");
        }

        System.out.println(".gitignore created at: " + gitignoreFile.getAbsolutePath());
    }
}
