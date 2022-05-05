package xyz.ufifty50sh.pyva;

public class PyVaLetterMatcher extends AbstractPyVaCharacterMatcher {

    public PyVaLetterMatcher() {
        super("Letter");
    }

    @Override
    protected boolean acceptChar(char c) {
        return Character.isJavaIdentifierStart(c);
    }
}