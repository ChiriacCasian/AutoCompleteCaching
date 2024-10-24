package com.github.chiriaccasian.autocompletecaching.Frontend;

import com.github.chiriaccasian.autocompletecachingkotlinpackage.SimpleInlineCompletionProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ClearCacheTreeButton extends AnAction {
    /**
     * Constructor
     * @param s text of button
     */
    ClearCacheTreeButton(String s){
        super(s);
    }

    /**
     * what to do when clicked, clears cache tree
     */
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("clear cache");
        SimpleInlineCompletionProvider.getInstance().getCacheClientInstance().clearCache() ;
    }
}
