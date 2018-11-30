        import com.intellij.lang.typescript.compiler.TypeScriptCompilerService;
        import com.intellij.lang.typescript.compiler.languageService.protocol.commands.TypeScriptFileLocationRequestArgs;
        import com.intellij.lang.javascript.service.protocol.LocalFilePath;
        import com.intellij.openapi.actionSystem.*;
        import com.intellij.openapi.editor.Caret;
        import com.intellij.openapi.editor.Editor;
        import com.intellij.openapi.editor.LogicalPosition;
        import com.intellij.openapi.project.Project;
        import com.intellij.openapi.ui.MessageType;
        import com.intellij.openapi.ui.Messages;
        import com.intellij.openapi.ui.popup.Balloon;
        import com.intellij.openapi.ui.popup.BalloonBuilder;
        import com.intellij.openapi.ui.popup.JBPopupFactory;
        import com.intellij.openapi.vfs.VirtualFile;
        import org.jetbrains.annotations.NotNull;

        import javax.swing.*;
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
                    showFutureString(project, result, caret);
                }
            }

            private void showFutureString(Project project, Future<String> result, Caret caret) {
                JBPopupFactory popupFactory = JBPopupFactory.getInstance();
                try{
                    String message = result.get(5, TimeUnit.SECONDS);

                    ShowTypeAction.showBaloon(
                            popupFactory
                                    .createDialogBalloonBuilder(new JLabel(message),"TypeScript Type Info")
                                    .setHideOnAction(true),
                            caret.getEditor());
                } catch (TimeoutException e){
                    ShowTypeAction.showBaloon(
                            popupFactory.createHtmlTextBalloonBuilder(
                    "TypeScript Language Server did not respond",
                                MessageType.ERROR, null),
                            caret.getEditor());
                } catch (InterruptedException e){
                    ShowTypeAction.showBaloon(
                            popupFactory.createHtmlTextBalloonBuilder(e.toString(), MessageType.ERROR, null),
                            caret.getEditor());
                } catch (ExecutionException e) {
                    ShowTypeAction.showBaloon(
                            popupFactory.createHtmlTextBalloonBuilder(e.toString(), MessageType.ERROR, null),
                            caret.getEditor());
                }
            }

            private static void showBaloon(BalloonBuilder builder, Editor editor){
                builder.createBalloon().show(
                        JBPopupFactory.getInstance().guessBestPopupLocation(editor),
                        Balloon.Position.above);
            }
        }