package maxlich.app.view;

import maxlich.app.controller.Controller;
import maxlich.app.model.PathContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;

public class ViewWindowGUI extends JFrame implements View {
    private static final int WINDOW_WIDTH = 750,WINDOW_HEIGHT = 500;
    private static final String DEFAULT_FILE_EXTENSION = "log";
    private static final String PATH_TO_IMAGE_OPEN = File.separator + "images" + File.separator + "folder-open_16.png";

    private Controller controller;
    private JTextField textFieldTextToFind;
    private JCheckBox checkBoxMatchCase;
    private JTree treeDirs;
    private JTextArea textAreaFileContent;

    public ViewWindowGUI(String title, Controller controller) {
        super(title);
        this.controller = controller;
        initGUI();
    }

    public void initGUI() {
        //initSystemLookAndFeel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        //инициализация главной панели
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        setContentPane(mainPanel);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //инициализация трех панелей с компонентами: верхней, центральной и нижней
        initTopPanel();
        initCentralPanel();
        initBottomPanel();

        setVisible(true);
    }

    public void initSystemLookAndFeel() {
        try {
            String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            // устанавливаем LookAndFeel
            UIManager.setLookAndFeel(systemLookAndFeelClassName);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Can't use the specified look and feel on this platform.");
        } catch (Exception e) {
            System.err.println("Couldn't get specified look and feel, for some reason.");
        }
    }

    //инициализация верхней панели
    public void initTopPanel() {
        JPanel panelTop = new JPanel(new GridBagLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);

        JLabel labelInputFileName = new JLabel("Введите полный путь к каталогу с лог-файлами:");
        JTextField textFieldSourceDirName = new JTextField();

        JButton buttonFileName = new JButton("Выбрать", new ImageIcon(getClass().getResource(PATH_TO_IMAGE_OPEN)));

        JLabel labelInputFileExtension = new JLabel("Расширение файлов:");
        JTextField textFieldFileExtension = new JTextField("log");

        JLabel labelInputTextToFind = new JLabel("Введите текст, который нужно найти в лог-файлах:");
        textFieldTextToFind = new JTextField();

        checkBoxMatchCase = new JCheckBox("Учитывать регистр", false);
        checkBoxMatchCase.setHorizontalTextPosition(SwingConstants.LEFT);

        //секция для выбора папки
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        buttonFileName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(ViewWindowGUI.this) == JFileChooser.APPROVE_OPTION)
                    //если пользователь выбрал папку, то записываем путь к ней в текстовое поле textFieldSourceDirName
                    textFieldSourceDirName.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JButton buttonOK = new JButton("OK");

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dirName = textFieldSourceDirName.getText();

                //проверка заполнения поля для ввода директории, содержащей лог-файлы
                if (dirName == null || dirName.isEmpty()) {
                    JOptionPane.showMessageDialog(ViewWindowGUI.this,
                            "Вы не заполнили путь к каталогу, в котором надо искать лог-файлы." +
                                    " Заполните, пожалуйста, путь к каталогу.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //проверяется, существует ли указанный путь и является ли он директорией
                Path pathToDir = Paths.get(dirName);
                if (Files.notExists(pathToDir)) {
                    JOptionPane.showMessageDialog(ViewWindowGUI.this,
                            "Каталог, который Вы выбрали или к которому указали путь, не существует. " +
                                    "Повторите выбор каталога или ввод пути к нему.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!Files.isDirectory(pathToDir)) {
                    JOptionPane.showMessageDialog(ViewWindowGUI.this,
                            "Каталог, который Вы выбрали или к которому указали путь, в действительности не является каталогом. " +
                                    "Выберете другой каталог.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //проверка заполнения поля для ввода текста, который будет искаться в лог-файлах
                String textToFind = textFieldTextToFind.getText();
                if (textToFind == null || textToFind.isEmpty()) {
                    JOptionPane.showMessageDialog(ViewWindowGUI.this,
                            "Вы не ввели текст, который нужно найти в лог-файлах." +
                                    " Заполните, пожалуйста, соответствующее поле.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //проверка расширения файла
                String fileExtension = textFieldFileExtension.getText();
                if (fileExtension == null || fileExtension.isEmpty())
                    fileExtension = DEFAULT_FILE_EXTENSION;
                fileExtension = fileExtension.toLowerCase();

                boolean matchCase = checkBoxMatchCase.isSelected(); //чекбокс "учитывать регистр"

                controller.onSelectSourceDirAndText(pathToDir,fileExtension,textToFind,matchCase);
            }
        });

        //расстановка компонентов на верхней панели
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.6;
        panelTop.add(labelInputFileName, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.4;
        panelTop.add(labelInputFileExtension, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(5, 0, 5, 5);
        panelTop.add(textFieldSourceDirName, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        constraints.insets = new Insets(5, 0, 5, 10);
        panelTop.add(buttonFileName, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.3;
        constraints.insets = new Insets(5, 0, 5, 5);
        panelTop.add(textFieldFileExtension, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        panelTop.add(labelInputTextToFind, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0.7;
        constraints.insets = new Insets(0, 0, 5, 5);
        panelTop.add(textFieldTextToFind, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.weightx = 0.2;
        panelTop.add(checkBoxMatchCase, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = 3;
        constraints.weightx = 0.1;
        panelTop.add(buttonOK, constraints);
    }

    //иницалиазация центральной панели
    public void initCentralPanel() {
        GridLayout gridLayout = new GridLayout(1,2);
        gridLayout.setHgap(10);

        JPanel panelCenter = new JPanel(gridLayout);
        getContentPane().add(panelCenter, BorderLayout.CENTER);

        //иницализация компонента, отображающего дерево каталогов и файлов
        treeDirs = new JTree();
        treeDirs.setModel(null);
        treeDirs.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        treeDirs.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                if (node.getParent() == null || node.getAllowsChildren()) return;   //если это корневой элемент или директория,
                                                                                    // то выходим из метода
                Path path = ((PathContainer) node.getUserObject()).getPath();
                if (!node.getAllowsChildren() && Files.isRegularFile(path))
                    controller.onSelectFileToShow(path);
            }
        });

        JScrollPane scrollerTree = new JScrollPane(treeDirs);
        scrollerTree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollerTree.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //инициализация текстовой области, отображающей содержимое лог-файла
        textAreaFileContent = new JTextArea();
        textAreaFileContent.setLineWrap(true);
        textAreaFileContent.setWrapStyleWord(true);
        textAreaFileContent.setEditable(false);

        JScrollPane scrollerTextArea = new JScrollPane(textAreaFileContent);
        scrollerTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollerTextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelCenter.add(scrollerTree);
        panelCenter.add(scrollerTextArea);
    }

    //инициализация нижней панели (панели с кнопками навигации по тексту лог-файла)
    private void initBottomPanel() {
        JPanel panelNavThroughTextLogFile = new JPanel();
        panelNavThroughTextLogFile.setLayout(new BoxLayout(panelNavThroughTextLogFile,BoxLayout.X_AXIS));


        JButton buttonPrev = new JButton("Назад");
        JButton buttonNext = new JButton("Вперёд");
        JButton buttonSelAll = new JButton("Выделить всё");

        //добавляем кнопкам обработчиков событий
        buttonNext.addActionListener(new ButtonNextListener());
        buttonPrev.addActionListener(new ButtonPrevListener());
        buttonSelAll.addActionListener(new ButtonSellAllListener());

        //добавляем кнопки на навигационную панель
        panelNavThroughTextLogFile.add(Box.createHorizontalGlue());
        panelNavThroughTextLogFile.add(buttonPrev);
        panelNavThroughTextLogFile.add(Box.createHorizontalStrut(10));
        panelNavThroughTextLogFile.add(buttonNext);
        panelNavThroughTextLogFile.add(Box.createHorizontalStrut(30));
        panelNavThroughTextLogFile.add(buttonSelAll);

        //добавляем навигационную панель на главное окно программы
        getContentPane().add(panelNavThroughTextLogFile,BorderLayout.SOUTH);
    }

    //обновление View
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof TreeModel) {
            treeDirs.setModel((TreeModel) arg); //отображаем полученное дерево
        } else if (arg instanceof List) {
            //отображаем содержимое выделенного в дереве файла
            List<String> resultList = (List<String>) arg;
            textAreaFileContent.setText("");
            for (String str : resultList) {
                textAreaFileContent.append(str);
                textAreaFileContent.append("\n");
            }
        }
    }


    private int currPosInTextArea;
    //обработчик событий от кнопки "Вперёд"
    class ButtonNextListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String contentTextArea = textAreaFileContent.getText();
            if (!checkBoxMatchCase.isSelected()) contentTextArea = contentTextArea.toLowerCase();

            String word = textFieldTextToFind.getText();
            if (!checkBoxMatchCase.isSelected()) word = word.toLowerCase();

            int startPosition = contentTextArea.indexOf(word,currPosInTextArea);
            if (startPosition < 0)
                startPosition = contentTextArea.indexOf(word,0);
            currPosInTextArea = startPosition + word.length();

            if (currPosInTextArea >= contentTextArea.length())
                currPosInTextArea = 0;

            textAreaFileContent.grabFocus();
            textAreaFileContent.select(startPosition,currPosInTextArea);
        }
    }

    //обработчик событий от кнопки "Назад"
    class ButtonPrevListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String contentTextArea = textAreaFileContent.getText();
            if (!checkBoxMatchCase.isSelected()) contentTextArea = contentTextArea.toLowerCase();

            String word = textFieldTextToFind.getText();
            if (!checkBoxMatchCase.isSelected()) word = word.toLowerCase();

            int startPosition = contentTextArea.lastIndexOf(word,currPosInTextArea-word.length()-1);
            if (startPosition < 0)
                startPosition = contentTextArea.lastIndexOf(word,contentTextArea.length());
            currPosInTextArea = startPosition + word.length();

            textAreaFileContent.grabFocus();
            textAreaFileContent.select(startPosition,currPosInTextArea);

        }
    }


    //обработка события от кнопки "Выделить всё" - выделение всего текста в textAreaContentFile
    class ButtonSellAllListener implements ActionListener {
        private boolean isTextAreaSelect;

        @Override
        public void actionPerformed(ActionEvent e) {
            textAreaFileContent.grabFocus();
            if (!isTextAreaSelect) {
                textAreaFileContent.selectAll();
                isTextAreaSelect = true;
                ((JButton)e.getSource()).setText("Снять выделение");
            } else {
                int docLength = textAreaFileContent.getDocument().getLength();
                textAreaFileContent.select(docLength,docLength);
                isTextAreaSelect = false;
                ((JButton)e.getSource()).setText("Выделить всё");
            }
        }
    }
}
