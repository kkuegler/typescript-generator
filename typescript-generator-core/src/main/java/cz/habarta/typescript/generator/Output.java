
package cz.habarta.typescript.generator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Output {

    private final Writer writer;
    private final String name;
    private final boolean closeWriter;

    private Output(Writer writer, String name, boolean closeWriter) {
        this.writer = writer;
        this.name = name;
        this.closeWriter = closeWriter;
    }

    public Writer getWriter() {
        return writer;
    }

    public String getName() {
        return name;
    }

    public boolean shouldCloseWriter() {
        return closeWriter;
    }

    public static Output to(File file) {
        try {
            file.getParentFile().mkdirs();
            return new Output(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8),
                    file.toString(), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Output to(OutputStream outputStream) {
        return new Output(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), null, false);
    }

    public static Output to(Writer writer) {
        return new Output(writer, null, false);
    }

}
