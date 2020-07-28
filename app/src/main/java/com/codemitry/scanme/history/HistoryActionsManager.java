package com.codemitry.scanme.history;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HistoryActionsManager extends ViewModel {
    private static final String FILE = "history";
    private static final int MAX_SIZE = 10;

    Queue<HistoryAction> historyActions;

    private File filename;

    public HistoryActionsManager() {

    }

    public HistoryActionsManager(File path) {
        this.filename = new File(path, FILE);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        this.historyActions = null;

    }

    public Collection<HistoryAction> getHistoryActions() {
        if (historyActions == null) {
            historyActions = loadHistoryActions();
        }

        Queue<HistoryAction> actions = new LinkedList<>(historyActions);
        reverse(actions);
        return actions;
    }

    public void saveHistoryActions() {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(historyActions);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addHistoryAction(HistoryAction action) {
        if (historyActions == null) {
            this.historyActions = loadHistoryActions();
        }
        if (historyActions.size() >= MAX_SIZE) {
            historyActions.poll();
        }
        historyActions.offer(action);
    }

    private Queue<HistoryAction> loadHistoryActions() {
        Queue<HistoryAction> actions = new LinkedList<>();

        if (filename.exists()) {

            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                actions.addAll((Queue<HistoryAction>) ois.readObject());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return actions;
    }

    public void setPath(File path) {
        this.filename = new File(path, FILE);
    }


    public static <T> void reverse(Queue<T> q) {
        List<T> copy = new ArrayList<>(q);
        Collections.reverse(copy);
        q.clear();
        q.addAll(copy);
    }

}
