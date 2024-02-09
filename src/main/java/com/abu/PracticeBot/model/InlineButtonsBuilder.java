package com.abu.PracticeBot.model;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InlineButtonsBuilder {

    private InlineButtonsBuilder(){
    }

    /*
    InlineButtonsFactory.builder()
        .addRow()
            .button(name, callback)
            .button(name, callback)
        .addRow()
            .button(name, callback)
            .button(name, callback)
        .build();
     */

    public static InlineButtonsBuilder builder() {
        return new InlineButtonsBuilder();
    }

    private final Stack<List<InlineKeyboardButton>> rows = new Stack<>();

    public InlineButtonsBuilder addRow() {
        rows.push(new ArrayList<>());
        return this;
    }

    public InlineButtonsBuilder button(String name, String callback) {
        if (rows.empty()) addRow();

        var button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(callback);

        rows.peek().add(button);

        return this;
    }

    public List<List<InlineKeyboardButton>> build() {
        return List.copyOf(rows);
    }
}
