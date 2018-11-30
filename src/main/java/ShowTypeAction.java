        import com.intellij.lang.javascript.service.protocol.JSLanguageServiceAnswer;
        import com.intellij.lang.javascript.service.protocol.JSLanguageServiceObject;
        import com.intellij.lang.javascript.service.protocol.JSLanguageServiceSimpleCommand;
        import com.intellij.lang.typescript.compiler.TypeScriptCompilerService;
        import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptFileLocationRequestArgs;
        import com.intellij.lang.javascript.service.protocol.LocalFilePath;
        import com.intellij.openapi.actionSystem.*;
        import com.intellij.openapi.editor.Caret;
        import com.intellij.openapi.editor.LogicalPosition;
        import com.intellij.openapi.fileEditor.FileEditor;
        import com.intellij.openapi.project.Project;
        import com.intellij.openapi.ui.Messages;
        import com.intellij.openapi.vfs.VirtualFile;
        import com.intellij.psi.PsiElement;
        import org.jetbrains.annotations.NotNull;

        import java.util.concurrent.ExecutionException;
        import java.util.concurrent.Future;
        import java.util.concurrent.TimeUnit;
        import java.util.concurrent.TimeoutException;

        public class ShowTypeAction extends AnAction {
            public ShowTypeAction() {
                super("ShowType");
            }

            private TypeScriptFileLocationRequestArgs fileLocationRequestArgs(VirtualFile currentFile, LogicalPosition position){
                TypeScriptFileLocationRequestArgs args = new TypeScriptFileLocationRequestArgs();
                args.file = LocalFilePath.create(currentFile.getPath());
                args.line = position.line + 1;
                args.offset = position.column + 1;
                return args;
            }

            public void actionPerformed(@NotNull AnActionEvent event) {
                Project project = event.getProject();
                VirtualFile currentFile = event.getData(LangDataKeys.PSI_FILE).getVirtualFile();
                if (currentFile == null) {
                    Messages.showErrorDialog(project,"Current File is null.", "ShowType");
                    return;
                }

                Caret caret = event.getData(LangDataKeys.CARET);



                TypeScriptCompilerService tsserver = TypeScriptCompilerService.getServiceForFile(project, currentFile);

                Future<String> result = tsserver.sendCommand(
                        new TypeScriptQuickInfoCommand(fileLocationRequestArgs(
                                currentFile,
                                caret.getLogicalPosition())
                        ),
                        (serviceObject, answer) -> answer.getElement().getAsJsonObject("body").get("displayString").getAsString()
                        );
                if (result != null) {
                    showFutureString(project, result);
                }
            }

            private void showFutureString(Project project, Future<String> result) {
                try{
                    Messages.showMessageDialog(project, result.get(5, TimeUnit.SECONDS),"ShowType",Messages.getInformationIcon());
                } catch (TimeoutException e){
                    Messages.showErrorDialog(project,"TypeScript Language Server did not respond.","ShowType");
                } catch (InterruptedException e){
                    Messages.showErrorDialog(project, e.toString(), "ShowType: InterruptedException");
                } catch (ExecutionException e){
                    Messages.showErrorDialog(project, e.toString(), "ShowType: ExecutionException");
                }
            }
        }