package maxlich.app.controller;

import java.nio.file.Path;

public interface Controller {
    //метод ответа на события выбора пользователем директории, в которой надо искать лог-файлы,
    //расширения лог-файлов, текста, который нужно искать в файлах, и выбора, нужно ли учитывать регистр
    //sourceDir - директория, в которой надо искать лог-файлы
    //extension - расширение файлов с логами
    //textToFind - текст, который нужно искать в лог-файлов
    //mathCase - булево поле, обозначающее, нужно ли учитывать регистр текста, который надо найти
    void onSelectSourceDirAndText(Path sourceDir,String extension,String textToFind, boolean mathCase);

    void onSelectFileToShow(Path pathToFile);
}
