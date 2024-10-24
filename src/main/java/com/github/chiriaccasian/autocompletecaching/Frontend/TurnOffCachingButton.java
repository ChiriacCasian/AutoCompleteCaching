package com.github.chiriaccasian.autocompletecaching.Frontend;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import com.github.chiriaccasian.autocompletecachingkotlinpackage.SimpleInlineCompletionProvider;
public class TurnOffCachingButton extends AnAction {

    /**
     * Constructor
     * @param text the text on the button
     */
    public TurnOffCachingButton(String text){
        super(text) ;
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("turn off caching");
        SimpleInlineCompletionProvider.getInstance().setCachingEnabled(
                !SimpleInlineCompletionProvider.getInstance().getCachingEnabled()
        );
    }
}
