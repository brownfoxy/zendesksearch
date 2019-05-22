package com.zendesk;

import com.zendesk.search.console.CommandLineInterface;

/**
 * Created by phanindra on 22/05/19.
 */
public class App {
    /**
     * entry point to the Application
     * @param args
     */
    public static void main(String[] args) {
        CommandLineInterface commandLineApp = new CommandLineInterface();
        commandLineApp.run();
    }
}
