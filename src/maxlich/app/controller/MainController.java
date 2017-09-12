package maxlich.app.controller;

import maxlich.app.model.Model;

import java.nio.file.Path;

public class MainController implements Controller {
    private Model model;

    public MainController(Model model) {
        this.model = model;
    }

    @Override
    public void onSelectSourceDirAndText(Path sourceDir,String extension,String textToFind, boolean mathCase) {
        model.loadFileTree(sourceDir,extension,textToFind,mathCase);
    }

    @Override
    public void onSelectFileToShow(Path pathToFile) {
        model.loadContentOfSelectedFile(pathToFile);
    }
}
