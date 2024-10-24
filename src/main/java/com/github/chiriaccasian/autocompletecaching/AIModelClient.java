package com.github.chiriaccasian.autocompletecaching;

import io.github.ollama4j.*;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.*;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.impl.*;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.tools.*;
import io.github.ollama4j.OllamaResultStream;
import io.github.ollama4j.utils.*;
import io.github.ollama4j.types.*;
//import io.github.ollama4j.models.OllamaCommonRequestModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.github.ollama4j.models.chat.OllamaChatMessageRole.USER;

public class AIModelClient {
    private final OllamaAPI ollamaAPI;

    public AIModelClient() {
        System.out.println("started OLlama sever");
        this.ollamaAPI = new OllamaAPI("http://localhost:11434");
    }

    /**
     * Sends a prompt to the Ai model and returns the output
     * @param input the prompt
     * @return the prompt output
     */
    private String fetchChatResult(String input) throws OllamaBaseException, IOException, InterruptedException {
        OllamaChatMessage ollamaChatMessage = new OllamaChatMessage();
        ollamaChatMessage.setRole(USER);
        ollamaChatMessage.setContent(input);
        OllamaChatRequestModel requestModel = new OllamaChatRequestModel("llama3.2", new ArrayList<>(List.of(ollamaChatMessage))); /// be carefull not to use and immutable List.of(), make sure to pass on a new List object
        OllamaChatResult result = ollamaAPI.chat(requestModel);
        return result.getResponse();
    }

    /**
     * Specialized function that parses the prompt result and returns just the suggestion
     * @param input the suggestion key
     * @return the suggestion
     */
    public String fetchAISuggestion(String input) throws OllamaBaseException, IOException, InterruptedException {
        String promptResponse = fetchChatResult("Include nothing else in the message except the code completion in this format suggestion : your_suggestion, Give me a code completion suggestion for : " + input) ;
        return promptResponse ;
    }
}