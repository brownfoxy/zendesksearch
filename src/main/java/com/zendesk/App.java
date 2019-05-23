package com.zendesk;

import com.zendesk.search.console.CommandLineInterface;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by phanindra on 22/05/19.
 */
public class App {
    /**
     * entry point to the Application
     * @param args
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        CommandLineInterface commandLineApp = new CommandLineInterface();
        commandLineApp.run();
    }
}
