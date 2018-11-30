import com.intellij.lang.javascript.ecmascript6.TypeScriptResolveProcessor;
import com.intellij.lang.javascript.service.JSLanguageServiceCommandProcessor;
import com.intellij.lang.javascript.service.protocol.JSLanguageServiceSimpleCommand;
import com.intellij.lang.typescript.compiler.TypeScriptCompilerService;
import com.intellij.lang.typescript.compiler.languageService.TypeScriptLanguageServiceEvents;
import com.intellij.lang.typescript.compiler.languageService.TypeScriptLanguageServiceUtil;
import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptCompletionsCommand;
import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptFileLocationRequestArgs;
import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptGetDefinitionCommand;
import com.intellij.lang.javascript.service.protocol.LocalFilePath;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

public class HelloAction extends AnAction {
    public HelloAction() {
        super("ShowType");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        PsiElement element = event.getData(LangDataKeys.PSI_ELEMENT);
        FileEditor editor = event.getData(LangDataKeys.FILE_EDITOR);
        Caret caret = event.getData(LangDataKeys.CARET);
        TypeScriptCompilerService tscompiler = null;
        if (element.getContainingFile().getVirtualFile()!= null) {
            tscompiler = TypeScriptCompilerService.getServiceForFile(project,
                       element.getContainingFile().getVirtualFile());
        TypeScriptFileLocationRequestArgs args = new TypeScriptFileLocationRequestArgs();
        args.file = LocalFilePath.create(element.getContainingFile().getVirtualFile().getPath());
        args.line = caret.getLogicalPosition().line;
        args.offset = caret.getLogicalPosition().column;

        System.out.println("HERE");
        tscompiler.sendCommand(new TypeScriptTypeDefinitionCommand(args),(serviceObject, answer) -> {
            String message = "Position " + caret.getLogicalPosition();
            Messages.showMessageDialog(project, message, "Greeting", Messages.getInformationIcon());
            return "";
        });
        }
    }
}