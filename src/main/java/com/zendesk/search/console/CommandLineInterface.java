package com.zendesk.search.console;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.util.Arrays;

/**
 * Created by phanindra on 22/05/19.
 */
public class CommandLineInterface {

    public void run() {
        TextIO textIO = TextIoFactory.getTextIO();

        TextTerminal<?> textTerminal = textIO.getTextTerminal();
        textTerminal.println("Welcome to Zendesk Search");
        textTerminal.println("Type 'quit' at any time, Press 'enter' to continue");
        textTerminal.println();
        textTerminal.println();
        textTerminal.println("Select Search Options:");
        textTerminal.print(Arrays.asList("* Press 1 to search Zendesk", "* Press 2 to view list of searchable fields", "* Press 'quit' to exit"));
        textTerminal.println();
        textTerminal.println();

        while (true) {
            String selected = textIO.newStringInputReader()
                    .read();
            if (selected.trim().equalsIgnoreCase("quit")) {
                textTerminal.println("exiting from the app...");
                System.exit(1);
            }
            int intOption;
            try {
                intOption = Integer.parseInt(selected);
            } catch (NumberFormatException nfe) {
                textTerminal.println("Invalid input!");
                continue;
            }
            if (intOption==1) {
                textTerminal.println(String.format("User selected %d", intOption));
            } else if (intOption == 2) {
                textTerminal.println(String.format("User selected %d", intOption));
            } else {
                textTerminal.println("Invalid input!");
            }
        }
    }
}
