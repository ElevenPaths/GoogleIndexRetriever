package com.elevenpaths.googleindexretriever.exceptions;

import java.util.ResourceBundle;

public class EmptyQueryException extends Exception {
    public EmptyQueryException () {
        super(ResourceBundle.getBundle("strings").getString("emptyQueryException"));
    }

    public EmptyQueryException (String message) {
        super(message);
    }

}