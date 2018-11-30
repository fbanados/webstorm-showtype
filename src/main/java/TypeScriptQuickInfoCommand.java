import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptCommandWithArguments;
import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptFileLocationRequestArgs;
import org.jetbrains.annotations.NotNull;

class TypeScriptQuickInfoCommand extends TypeScriptCommandWithArguments<TypeScriptFileLocationRequestArgs> {
    private static final String COMMAND = "quickinfo";

    TypeScriptQuickInfoCommand(@NotNull TypeScriptFileLocationRequestArgs args) {
        super("quickinfo", args);
    }
}

