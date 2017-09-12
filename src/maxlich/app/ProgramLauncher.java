package maxlich.app;

import maxlich.app.controller.Controller;
import maxlich.app.controller.MainController;
import maxlich.app.model.MainModel;
import maxlich.app.model.Model;
import maxlich.app.view.View;
import maxlich.app.view.ViewWindowGUI;

public class ProgramLauncher {
    public static void main(String[] args) {
        Model model = new MainModel();
        Controller controller = new MainController(model);
        View view = new ViewWindowGUI("Поиск текста в лог-файлах",controller);
        model.addObserver(view);

    }
}
