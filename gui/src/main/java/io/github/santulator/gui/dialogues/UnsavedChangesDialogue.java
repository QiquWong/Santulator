/*
 * Open Source Software published under the Apache Licence, Version 2.0.
 */

package io.github.santulator.gui.dialogues;

import io.github.santulator.gui.common.GuiConstants;
import io.github.santulator.gui.common.UnsavedResponse;
import io.github.santulator.session.FileNameTool;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.santulator.gui.view.CssTool.applyCss;

public class UnsavedChangesDialogue {
    private static final String ALERT_ID = "unsavedChanges";

    private final Path file;

    private final Map<ButtonType, UnsavedResponse> map = unsavedResponseMap();

    private UnsavedResponse result;

    public UnsavedChangesDialogue(final Path file) {
        this.file = file;
    }

    public void showDialogue() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String message = String.format("'%s' has been modified.  Do you want to save your changes?", filename());

        alert.setTitle("Unsaved Changes");
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(map.keySet());
        alert.getDialogPane().setId(ALERT_ID);
        applyCss(alert);

        result = alert.showAndWait()
            .map(map::get)
            .orElse(UnsavedResponse.CANCEL);
    }

    private String filename() {
        if (file == null) {
            return GuiConstants.UNTITLED;
        } else {
            return FileNameTool.filename(file);
        }
    }

    public UnsavedResponse getUserResponse() {
        return result;
    }

    private Map<ButtonType, UnsavedResponse> unsavedResponseMap() {
        Map<ButtonType, UnsavedResponse> map = new LinkedHashMap<>();

        map.put(new ButtonType("Save"), UnsavedResponse.SAVE);
        map.put(new ButtonType("Discard"), UnsavedResponse.DISCARD);
        map.put(new ButtonType("Cancel"), UnsavedResponse.CANCEL);

        return map;
    }
}
