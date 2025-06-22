import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.MalformedInputException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//COMPILE_OPTIONS --enable-preview --release 24
//RUNTIME_OPTIONS --enable-preview

//JAVA 24

//DEPS org.json:json:20250517

Map<String, String> replacements = new HashMap<>();
JSONObject license = null;
Path root = Path.of(".").toAbsolutePath();

void main() throws IOException, InterruptedException {
    replacements.putAll(loadReplacementsFromEnv());
    license = loadLicense();
    replacements.put("LICENSE_URL", license.getString("html_url"));

    Files.walkFileTree(root, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    Path newPath = replacePath(path);
                    if (path.toAbsolutePath().startsWith(root.resolve(".github")) || path.getFileName().equals(Path.of("README.md"))) {
                        return FileVisitResult.CONTINUE;
                    }

                    Files.createDirectories(newPath.getParent());

                    if (!newPath.equals(path)) Files.move(path, newPath);

                    try {
                        String content = Files.readString(newPath);
                        Files.deleteIfExists(newPath);
                        Files.writeString(newPath, replaceContent(content), StandardOpenOption.CREATE);
                    } catch (MalformedInputException _) {
                        // ignore if no text file
                        return FileVisitResult.CONTINUE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });


    Files.move(root.resolve("README.md"), Path.of("SETUP.md"));
    copyDefault("README.md", Path.of("."));
    createLicenseFile();

}

void createLicenseFile() throws IOException {
    String content = license.getString("body");
    String name = replacements.get("LICENSE_NAME");

    content = switch (name) {
        case "MIT" -> content.replace("[fullname]", replacements.get("AUTHOR_NAME")).replace("[year]", String.valueOf(LocalDate.now().getYear()));
        default -> content;
    };

    Path path = root.resolve("LICENSE");
    Files.deleteIfExists(path);
    Files.writeString(path, content, StandardOpenOption.CREATE);
}

void copyDefault(String name, Path pathFromRoot) throws IOException {
    String s = Files.readString(root.resolve(".github/workflows/setup-repo/defaults").resolve(name));

    Path path = root.resolve(pathFromRoot).resolve(name);
    Files.deleteIfExists(path);
    Files.writeString(path, replaceContent(s), StandardOpenOption.CREATE);
}

Path replacePath(Path path) {
    String pathString = path.toString();
    String replaced = replaceContent(pathString);
    return Path.of(replaced);
}

String replaceContent(String content) {
    String text = content;
    for (Map.Entry<String, String> entry : replacements.entrySet()) {
        text = text.replace(entry.getKey(), entry.getValue());
    }
    return text;
}

Map<String, String> loadReplacementsFromEnv() {
    return Map.ofEntries(
            projectName(),
            Map.entry("PROJECT_LOWER_NAME", projectName().getValue().toLowerCase()),
            a("PROJECT_DESC"),
            a("JAVA_VERSION"),
            a("LICENSE_NAME"),
            aOpt("AUTHOR_NAME", a("REPO_OWNER").getValue()),
            a("REPO_URL"),
            a("REPO_OWNER"),
            a("REPO_NAME"),
            a("MVN_GROUP"),
            aOpt("MVN_ARTIFACT", projectName().getValue().toLowerCase()),
            repoWoOwnerName()
    );
}

Map.Entry<String, String> repoWoOwnerName() {
    return Map.entry("REPO_WO_OWNER_NAME", a("REPO_NAME").getValue().split("/")[1]);
}

Map.Entry<String, String> projectName() {
    return aOpt("PROJECT_NAME", repoWoOwnerName().getValue());
}

Map.Entry<String, String> aOpt(String key, String fallback) {
    Map.Entry<String, String> loaded = a(key);
    if (loaded.getValue().equals(key)) return Map.entry(key, fallback);
    return loaded;
}

Map.Entry<String, String> a(String key) {
    String env = System.getenv(key);
    return Map.entry(key, env != null && !env.isBlank() ? env : key);
}

HttpClient client = HttpClient.newHttpClient();
JSONObject loadLicense() throws IOException, InterruptedException {
    if (license != null) return license;
    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.github.com/licenses/%s".formatted(replacements.get("LICENSE_NAME"))))
            .header("Accept", "application/vnd.github+json")
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("GH-API response: " + response.statusCode());

    return new JSONObject(response.body());

}



