import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptCommandWithArguments;
import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptFileLocationRequestArgs;
import org.jetbrains.annotations.NotNull;

public class TypeScriptTypeDefinitionCommand extends TypeScriptCommandWithArguments<TypeScriptFileLocationRequestArgs> {
    private static final String COMMAND = "typeDefinition";

    public TypeScriptTypeDefinitionCommand(@NotNull TypeScriptFileLocationRequestArgs args) {
        super("typeDefinition", args);
    }
}

