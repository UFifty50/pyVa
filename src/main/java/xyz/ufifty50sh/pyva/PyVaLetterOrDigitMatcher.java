package xyz.ufifty50sh.pyva;

public class PyVaLetterOrDigitMatcher extends AbstractPyVaCharacterMatcher {

    public PyVaLetterOrDigitMatcher() {
        super("LetterOrDigit");
    }

    @Override
    protected boolean acceptChar(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}