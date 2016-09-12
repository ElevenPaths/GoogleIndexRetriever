package com.elevenpaths.googleindexretriever.exceptions;

import java.util.ResourceBundle;

public class ManyResultsException extends Exception {
    public ManyResultsException(){
        super(ResourceBundle.getBundle("strings").getString("manyResults"));
    }

    public ManyResultsException(String message) {
        super(message);
    }

}