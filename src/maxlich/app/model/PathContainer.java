package maxlich.app.model;

import java.nio.file.Path;

public class PathContainer {
    private Path path;

    public PathContainer(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }
}
