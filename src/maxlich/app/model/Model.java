package maxlich.app.model;

import java.nio.file.Path;
import java.util.Observable;

public abstract class Model extends Observable {
    //метод, загружающий в модель дерево каталогов и файлов (файлы отобраны по некоторым параметрам)
    //sourceDir - корневой каталог дерева, extensionFiles - расширения файлов, попадающих в дерево,
    //textToFindIntoFiles - текст, который должны содержать все файлы, mathCaseOfTextToFind - учитывать ли регистр текста
    public abstract void loadFileTree(Path sourceDir, String extensionFiles,
                                      String textToFindIntoFiles, boolean matсhCaseOfTextToFind);

    public abstract void loadContentOfSelectedFile(Path pathToFile);
}
