package me.cometkaizo.brokenarrows;

import me.cometkaizo.util.StringUtils;

public interface Diagnostic {
    String getString();

    record Error(Throwable e, String message) implements Diagnostic {
        @Override
        public String getString() {
            return message + "\n\n" + StringUtils.getAbbreviatedMessage(e);
        }
    }
}
