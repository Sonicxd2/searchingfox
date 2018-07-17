package ru.searchingfox.server.utils;

import jline.console.ConsoleReader;
import lombok.extern.java.Log;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

@Log
public class Console {

    public HashMap<String, Consumer<String[]>> commands = new HashMap<>();

    public void addCommand(Consumer<String[]> command, String... names) {
        synchronized (commands) {
            for (String name : names) {
                commands.put(name, command);
            }
        }
    }


    public void run() {
        AnsiConsole.systemInstall();
        ConsoleReader consoleReader = null;
        try {
            consoleReader = new ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        consoleReader.setExpandEvents(false);

        while(!Thread.interrupted()) {
            String command;
            try {
                command = consoleReader.readLine("> ");
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if(command.isEmpty()) {
                continue;
            }

            String[] subCommands = command.split(" ");

            Consumer<String[]> commandExecutor = null;

            synchronized (commands) {
                commandExecutor = commands.get(subCommands[0]);
            }

            if(commandExecutor == null) {
                log.info("Command not found!");
                continue;
            }

            commandExecutor.accept(Arrays.copyOfRange(subCommands, 1, subCommands.length));
        }
    }
}