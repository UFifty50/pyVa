package xyz.ufifty50sh.pyva;

import org.parboiled.annotations.*;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.parboiled.Rule;

@BuildParseTree
public class PyVaMain extends BaseParser<Object> {
    public Rule CompilationUnit() {
        return Sequence(
            Spacing(),
            Optional(PackageDeclaration()),
            ZeroOrMore(ImportDeclaration()),
            ZeroOrMore(TypeDeclaration()),
            EOI
        );
    }
        Rule PackageDeclaration() {
            return Sequence(PACKAGE, QualifiedIdentifier(), SEMI);
        }

        Rule ImportDeclaration() {
            return FirstOf(
                            Sequence(IMPORT, Optional(STATIC),
                                    QualifiedIdentifier(),
                                    Optional(DOT, STAR), 
                                    Optional(ZeroOrMore(AS, Identifier())), 
                                    SEMI),
                            Sequence(FROM, QualifiedIdentifier(),
                                    IMPORT, QualifiedIdentifier(), 
                                    Optional(ZeroOrMore(AS, Identifier())),
                                    SEMI)
            );
        }

        Rule TypeDeclaration() {
            return FirstOf(ClassDeclaration(), EnumDeclaration());
        }

        Rule ClassDeclaration() {
            return Sequence(ZeroOrMore(ABSTRACT, PUBLIC, FINAL), CLASS,
            Identifier());
        }

        Rule EnumDeclaration() {
            return null;
        }

        final Rule AT = Terminal("@");
        final Rule ANDAND = Terminal("&&");
        final Rule BANG = Terminal("!", Ch('='));
        final Rule COMMA = Terminal(",");
        final Rule DEC = Terminal("--");
        final Rule DIV = Terminal("/", Ch('='));
        final Rule DIVEQU = Terminal("/=");
        final Rule DOT = Terminal(".");
        final Rule EQU = Terminal("=", Ch('='));
        final Rule EQUAL = Terminal("==");
        final Rule GE = Terminal(">=");
        final Rule GT = Terminal(">", AnyOf("=>"));
        final Rule HAT = Terminal("^", Ch('='));
        final Rule HATEQU = Terminal("^=");
        final Rule INC = Terminal("++");
        final Rule LBRK = Terminal("[");
        final Rule LE = Terminal("<=");
        final Rule LPAR = Terminal("(");
        final Rule LPOINT = Terminal("<");
        final Rule LT = Terminal("<", AnyOf("=<"));
        final Rule LWING = Terminal("{");
        final Rule MINUS = Terminal("-", AnyOf("=-"));
        final Rule MINUSEQU = Terminal("-=");
        final Rule MOD = Terminal("%", Ch('='));
        final Rule MODEQU = Terminal("%=");
        final Rule NOTEQUAL = Terminal("!=");
        final Rule OR = Terminal("|");
        final Rule PLUS = Terminal("+", AnyOf("=+"));
        final Rule PLUSEQU = Terminal("+=");
        final Rule RBRK = Terminal("]");
        final Rule RPAR = Terminal(")");
        final Rule RWING = Terminal("}");
        final Rule SEMI = Terminal(";");
        final Rule STAR = Terminal("*", Ch('='));
        final Rule STAREQU = Terminal("*=");
    
        public final Rule FROM = Keyword("from");
        public final Rule AS = Keyword("as");
        public final Rule ASSERT = Keyword("assert");
        public final Rule BREAK = Keyword("break");
        public final Rule CASE = Keyword("case");
        public final Rule CATCH = Keyword("catch");
        public final Rule CLASS = Keyword("class");
        public final Rule CONTINUE = Keyword("continue");
        public final Rule DEFAULT = Keyword("default");
        public final Rule DO = Keyword("do");
        public final Rule ELSE = Keyword("else");
        public final Rule ENUM = Keyword("enum");
        public final Rule EXTENDS = Keyword("extends");
        public final Rule FINALLY = Keyword("finally");
        public final Rule FINAL = Keyword("final");
        public final Rule FOR = Keyword("for");
        public final Rule IF = Keyword("if");
        public final Rule IMPLEMENTS = Keyword("implements");
        public final Rule IMPORT = Keyword("import");
        public final Rule INTERFACE = Keyword("interface");
        public final Rule INSTANCEOF = Keyword("instanceof");
        public final Rule NEW = Keyword("new");
        public final Rule PACKAGE = Keyword("package");
        public final Rule RETURN = Keyword("return");
        public final Rule STATIC = Keyword("static");
        public final Rule SUPER = Keyword("super");
        public final Rule SWITCH = Keyword("switch");
        public final Rule SYNCHRONIZED = Keyword("synchronized");
        public final Rule THIS = Keyword("this");
        public final Rule THROWS = Keyword("throws");
        public final Rule THROW = Keyword("throw");
        public final Rule TRY = Keyword("try");
        public final Rule VOID = Keyword("void");
        public final Rule WHILE = Keyword("while");
        public final Rule ABSTRACT = Keyword("abstract");
        public final Rule PUBLIC = Keyword("public");
    
        @MemoMismatches
        public Rule Keyword() {
            return Sequence(
                FirstOf("abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default", "do", "else",
                        "enum", "extends", "finally", "final", "for", "goto", "if", "implements", "import", "interface",
                        "instanceof", "new", "package", "return", "static", "super", "switch", "synchronized", "this",
                        "throws", "throw", "try", "void", "while", "as", "from"),
                TestNot(LetterOrDigit())
            );
        }
    
        @SuppressNode
        @DontLabel
        public Rule Keyword(String keyword) {
            return Terminal(keyword, LetterOrDigit());
        }
    
        @SuppressNode
        @DontLabel
        public Rule Terminal(String string) {
            return Sequence(string, Spacing()).label('\'' + string + '\'');
        }
    
        @SuppressNode
        @DontLabel
        public Rule Terminal(String string, Rule mustNotFollow) {
            return Sequence(string, TestNot(mustNotFollow), Spacing()).label('\'' + string + '\'');
        }
    
        @SuppressNode
        @MemoMismatches
        public Rule LetterOrDigit() {
            // switch to this "reduced" character space version for a ~10% parser performance speedup
            return FirstOf(Sequence('\\', UnicodeEscape()), new PyVaLetterOrDigitMatcher());
        }
    
        @SuppressNode
        Rule Spacing() {
            return ZeroOrMore(FirstOf(
    
                    // whitespace
                    OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),
    
                    // traditional comment
                    Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),
    
                    // end of line comment
                    Sequence(
                            "//",
                            ZeroOrMore(TestNot(AnyOf("\r\n")), ANY),
                            FirstOf("\r\n", '\r', '\n', EOI)
                    )
            ));
        }
    
        public Rule UnicodeEscape() {
            return Sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
        }
    
        public Rule HexDigit() {
            return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
        }
    
        @SuppressSubnodes
        @MemoMismatches
        Rule Identifier() {
            return Sequence(TestNot(Keyword()), Letter(), ZeroOrMore(LetterOrDigit()), Spacing());
        }
    
        Rule Letter() {
            // switch to this "reduced" character space version for a ~10% parser performance speedup
            return FirstOf(Sequence('\\', UnicodeEscape()), new PyVaLetterMatcher());
        }
    
        Rule QualifiedIdentifier() {
            return Sequence(Identifier(), ZeroOrMore(DOT, Identifier()));
        }

    public static void main(String[] args) {
        String input = String.join("\n"
        , "package cc;"
        , "import totalAnihilation;"
        , "import t as tt;"
        , "from t import x as a;"
        );
        PyVaMain parser = Parboiled.createParser(PyVaMain.class);
        ParsingResult<?> result = new ReportingParseRunner(parser.CompilationUnit()).run(input);
        System.out.println(result.hasErrors());
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        System.out.println(parseTreePrintOut);
    }
}