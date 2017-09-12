package maxlich.app.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainModel extends Model {
    private String extensionOfFiles;
    private String textToFind;
    private boolean matchCase;

    @Override
    public void loadFileTree(Path sourceDir, String extensionFiles, String textToFind, boolean matсhCase) {

        this.extensionOfFiles = extensionFiles;
        this.textToFind = textToFind;
        this.matchCase = matсhCase;

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(sourceDir,true);

        addChildrenNodes(rootNode,sourceDir);
        deleteAllEmptyBranches(rootNode);

        TreeModel treeModel = new DefaultTreeModel(rootNode,true);

        setChanged();
        notifyObservers(treeModel);
    }

    //ищет и добавляет "детей" узлам дерева
    private void addChildrenNodes(DefaultMutableTreeNode rootNode, Path sourceDir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path path: stream) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString().toLowerCase(); //имя файла в нижнем регистре
                    if (fileName.endsWith("."+extensionOfFiles) && isFileContainsText(path)) //если файл имеет нужное расширение и содержит искомый текст
                        rootNode.add(new DefaultMutableTreeNode(new PathContainer(path),false)); //то добавляет его в дерево
                } else if (Files.isDirectory(path)) {
                    if (path.toFile().listFiles().length == 0) continue;
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new PathContainer(path), true);
                    rootNode.add(newChild); //добавляем папку в дерево
                    addChildrenNodes(newChild,path); //рекурсивный вызов метода с данной папкой и данным узлом в качестве параметров
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
    }

    //метод проверяет, содержит ли файл  искомый текст
    private boolean isFileContainsText(Path file) throws IOException {
        try {
            List<String> fileContent = Files.readAllLines(file);
            if (matchCase) {
                for (String str : fileContent) {
                    if (str.contains(textToFind)) {
                        return true;
                    }
                }
            } else {
                for (String str : fileContent) {
                    if (str.toLowerCase().contains(textToFind.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (MalformedInputException e) {}

        return false;
    }

    private void deleteAllEmptyBranches(DefaultMutableTreeNode currNode) {
        //Path pathFromCurrNode = ((PathContainer) currNode.getUserObject()).getPath();
        if (currNode == null) return;

        if (currNode.getAllowsChildren() /*&& Files.isDirectory(pathFromCurrNode)*/) {
            if (currNode.isLeaf()) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currNode.getParent();
                currNode.removeFromParent();
                deleteAllEmptyBranches(parent);
            } else {
                Enumeration children = currNode.children();
                while (children.hasMoreElements())
                    deleteAllEmptyBranches((DefaultMutableTreeNode)children.nextElement());
            }
        }
    }

    //метод загружает в список строки из файла и передаёт этот список вьюхам для отображения
    @Override
    public void loadContentOfSelectedFile(Path pathToFile) {
        try {
            List<String> fileContent = Files.readAllLines(pathToFile);
            setChanged();
            notifyObservers(fileContent);
        } catch (IOException e) {
            System.err.println(e);
            setChanged();
            notifyObservers( Collections.singletonList("Ошибка чтения файла") );
        }
    }
}
