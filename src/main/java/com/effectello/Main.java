package com.effectello;

import com.effectello.commands.Clone;
import picocli.CommandLine;

public class Main {

    public static void main( String[] args ) {
        System.exit(new CommandLine(new Clone()).execute(args));
    }
}
