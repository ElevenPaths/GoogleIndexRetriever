package com.elevenpaths.googleindexretriever.exceptions;

import java.util.ResourceBundle;

public class CaptchaException extends Exception {
    public CaptchaException () {
        super(ResourceBundle.getBundle("strings").getString("resolveCaptcha"));
    }
    public CaptchaException (String message) {
        super(message);
    }

}