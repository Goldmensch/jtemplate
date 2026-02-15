///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.jline:jline:3.25.1
//DEPS org.json:json:20250517
//JAVA 25
//SOURCES Project.java
//SOURCES ReplacePlaceholder.java
//SOURCES RootPath.java
//SOURCES Util.java

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.nio.file.Files;

Terminal terminal;

void main() throws Exception {
    terminal = TerminalBuilder.builder().system(true).build();

    try {
        Project project = new Project(
                read("Project name (default repository name)", false),
                read("Project description", true),
                readInt("Used Java version"),
                read("Project author name (default repository owner)", false),
                read("Maven group", true),
                read("Maven artifact (default repository name)", false),
                read("Project license (TAB for list)", true, Project.LICENSES)
        );

        // replace placeholders
        IO.println("Replacing placeholders in repository...");
        ReplacePlaceholder.replace(project);

        // remove setup files
        IO.println("Removing setup files...");
        Files.deleteIfExists(RootPath.ROOT.resolve(".github/workflows/create_metadata_file.yml"));
        Files.deleteIfExists(RootPath.ROOT.resolve("setup"));
        Files.deleteIfExists(RootPath.ROOT.resolve("repo_metadata"));
        Util.deleteDirectory(RootPath.ROOT.resolve("setup_code"));

        // git: commit changes
        new ProcessBuilder()
                .command("git add .", "git commit -m 'Prepare repository'")
                .start();

    } catch (Abort _ ) {}
}

int readInt(String prompt) {
    String read = read(prompt, true);

    try {
        return Integer.parseUnsignedInt(read);
    } catch (NumberFormatException e) {
        System.err.println("Input must be number!");
        throw new Abort();
    }
}

String read(String prompt, boolean required, String... completions) {
    LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter(completions))
            .build();

    String input = reader.readLine(prompt + ": ").trim();
    if (input.isBlank() && required) {
        System.out.println("Input cannot be blank!");
        throw new Abort();
    }
    return input;
}

static class Abort extends RuntimeException {}



