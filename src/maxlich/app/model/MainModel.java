package maxlich.app.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
                    File[] listFiles = path.toFile().listFiles();
                    if (listFiles == null || listFiles.length == 0) continue;
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

    //метод очищает дерево от пустых веток (содержащих одни пустые каталоги)
    private void deleteAllEmptyBranches(DefaultMutableTreeNode currNode) {
        if (currNode == null) return;
        Enumeration<DefaultMutableTreeNode> children = currNode.children();
        if (children == null || !children.hasMoreElements()) return;

        Queue<DefaultMutableTreeNode> queue = new LinkedList<>();

        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = children.nextElement();
            if (child.getAllowsChildren())    //если это каталог, то добавляем его в очередь
                queue.add(child);
        }

        while (!queue.isEmpty()) {
            DefaultMutableTreeNode node = queue.poll();
            if (node == null)  continue;
            if (node.getAllowsChildren()) {
                if (node.isLeaf()) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    node.removeFromParent();
                    if (queue.contains(parent) || parent == null) continue; // если родитель этого узла уже есть в очереди
                                                                            // или он равен нулл, то ничего не делаем
                    queue.offer(parent); //иначе добавляем его в очередь
                } else {
                    children = node.children();
                    while (children.hasMoreElements()) {
                        DefaultMutableTreeNode child = children.nextElement();
                        if (child.getAllowsChildren() && !queue.contains(child))    // если это каталог и его ещё нет в очереди, то...
                            queue.add(child);                         // добавляем его в очередь
                    }
                }
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
