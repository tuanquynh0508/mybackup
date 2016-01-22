/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tnqsoft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author nntuan
 */
public class FileBackup {

    private static String sourcePath;
    private static String desPath;
    private static int currentTotalFile;
    private static int totalFile;
    private static JProgressBar progCopy;

    public FileBackup(String sourcePath, String desPath, JProgressBar progCopy) {
        this.sourcePath = sourcePath;
        this.desPath = desPath;
        this.currentTotalFile = 0;
        this.progCopy = progCopy;
        this.progCopy.setValue(0);
        this.progCopy.setStringPainted(true);
        this.totalFile = countFilesInDirectory(new File(this.sourcePath));
        this.progCopy.setMaximum(totalFile);
    }

    public void backup() throws IOException {
        copyFileOrFolder(new File(sourcePath), new File(desPath));
    }
 
    public void copyFileOrFolder(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            copyFolder(source, dest);
        } else {
            ensureParentFolder(dest);
            copyFile(source, dest);
        }
    }

    private void copyFolder(File source, File dest) throws IOException {
        if (!dest.exists()) {
            dest.mkdirs();
        }
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
                if (f.isDirectory() && isAllowFolder(f.getName())) {
                    copyFolder(f, newFile);
                } else {
                    copyFile(f, newFile);
                }
            }
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        currentTotalFile++;
        progCopy.setValue(currentTotalFile);
        progCopy.update(progCopy.getGraphics());
        Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);
    }

    private static void ensureParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    /**
     * Count files in a directory (including files in all subdirectories)
     *
     * @param directory the directory to start in
     * @return the total number of files
     */
    private int countFilesInDirectory(File directory) {
        int count = 0;
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (f.isDirectory() && isAllowFolder(f.getName())) {
                    count += countFilesInDirectory(f);
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isAllowFolder(String filename) {
        try {
            return !filename.matches("backup|bundles|logs|cache|vendor|bower_components|node_modules|.git|.svn");
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
}
