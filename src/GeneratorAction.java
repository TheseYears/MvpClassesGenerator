import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Mvp类生成器
 * Created by Ryan on 2016/9/6.
 */
public class GeneratorAction extends AnAction {

    private Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        GeneratorDialog dialog = new GeneratorDialog(project);
        dialog.show();
    }

    private void onOk(String className, boolean fragment, boolean paging, String subPackage) {
        new GeneratorWriter(project, className, subPackage, fragment, paging).execute();
    }

    private class GeneratorDialog extends DialogWrapper {

        private String[] myOptions = new String[] { "确定", "取消" };
        private int myDefaultOptionIndex = 0;
        private int myFocusedOptionIndex = 0;
        private JTextComponent classNameField, subPackageField, packageField;
        private JCheckBox fragmentCb, pagingCb;

        private GeneratorDialog(@Nullable Project project) {
            super(project);
            init();
            setTitle("生成");
        }

        @NotNull protected Action[] createActions() {
            Action[] actions = new Action[this.myOptions.length];

            for(int i = 0; i < this.myOptions.length; ++i) {
                final int position = i;
                String option = this.myOptions[i];
                actions[i] = new AbstractAction(UIUtil.replaceMnemonicAmpersand(option)) {
                    public void actionPerformed(ActionEvent e) {
                        if (position == 0) {
                            String packageName = packageField.getText().trim();
                            if (packageName.length() <= 0) return;
                            PropertiesComponent.getInstance().setValue(Settings.PROJECT_PACKAGE, packageName);

                            String sub = subPackageField.getText().trim();
//                            if (sub.length() <= 0) return;
                            PropertiesComponent.getInstance().setValue("sub", sub);

                            String name = classNameField.getText().trim();
                            if (name.length() <= 0) return;
                            onOk(name, fragmentCb.isSelected(), pagingCb.isSelected(), sub);
                        }
                        close(position, true);
                    }
                };
                if(i == this.myDefaultOptionIndex) {
                    actions[i].putValue("DefaultAction", Boolean.TRUE);
                }

                if(i == this.myFocusedOptionIndex) {
                    actions[i].putValue("FocusedAction", Boolean.TRUE);
                }

                UIUtil.assignMnemonic(option, actions[i]);
            }

            return actions;
        }

        @Nullable @Override protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(15, 5));
            JPanel content = new JPanel(new GridLayout(0, 1));
            JPanel boxes = new JPanel(new BorderLayout());

            JTextPane messageComponent1 = createMessageComponent("请输入类名:");
            content.add(messageComponent1);

            classNameField = new JTextField(30);
            content.add(this.classNameField);

            JTextPane messageComponent2 = createMessageComponent("请输入待生成的Activity/Fragment需在的包名: 如main");
            content.add(messageComponent2);

            subPackageField = new JTextField(30);
            content.add(this.subPackageField);
            subPackageField.setText(PropertiesComponent.getInstance().getValue("sub", ""));

            JTextPane messageComponent3 = createMessageComponent("请输入主模块包名:");
            content.add(messageComponent3);

            packageField = new JTextField(30);
            content.add(this.packageField);
            packageField.setText(Constant.getProjectPackage());

            fragmentCb = new JCheckBox("fragment");
            fragmentCb.setSelected(false);
            pagingCb = new JCheckBox("分页");
            pagingCb.setSelected(false);

            boxes.add(fragmentCb, "North");
            boxes.add(pagingCb, "West");

            panel.add(content, "North");
            panel.add(boxes);

            return panel;
        }

        private JTextPane createMessageComponent(String message) {
            JTextPane messageComponent = new JTextPane();
            return Messages.configureMessagePaneUi(messageComponent, message);
        }
    }

}
