package com.github.chiriaccasian.autocompletecaching.Frontend;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import com.github.chiriaccasian.autocompletecachingkotlinpackage.SimpleInlineCompletionProvider;

public class TopBarButton extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new TurnOffCachingButton("caching is : " + SimpleInlineCompletionProvider.getInstance().getCachingEnabled() ));
        actionGroup.add(new ClearCacheTreeButton("caching size is : " + SimpleInlineCompletionProvider.getInstance().getCacheClientInstance().getCacheSize() ));

        JBPopupFactory.getInstance()
                .createActionGroupPopup(
                        "Select an Option",
                        actionGroup,
                        anActionEvent.getDataContext(),
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        true
                ).showInBestPositionFor(anActionEvent.getDataContext());
    }
}