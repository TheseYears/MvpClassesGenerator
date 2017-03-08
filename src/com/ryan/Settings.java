package com.ryan;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.ryan.Constant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 设置菜单
 */
public class Settings implements Configurable {

    public static final String PROJECT_PACKAGE = "mvpclassesgenerator_project_package";
    public static final String CORELIBS_PACKAGE = "mvpclassesgenerator_corlibs_package";
    public static final String VIEW_PACKAGE = "mvpclassesgenerator_view_package";
    public static final String VIEW_INTERFACE_PACKAGE = "mvpclassesgenerator_interface_package";
    public static final String PRESENTER_PACKAGE = "mvpclassesgenerator_presenter_package";
    public static final String MAIN_MODULE_NAME = "mvpclassesgenerator_main_module_name";

    private JPanel mPanel;
    private JTextField f_corelibs_package;
    private JTextField f_project_package;
    private JTextField f_view_package;
    private JTextField f_interface_package;
    private JTextField f_presenter_package;
    private JTextField f_main_module_name;

    @Nls
    @Override
    public String getDisplayName() {
        return "MvpClassesGenerator";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        reset();
        return mPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(PROJECT_PACKAGE, f_project_package.getText());
        PropertiesComponent.getInstance().setValue(CORELIBS_PACKAGE, f_corelibs_package.getText());
        PropertiesComponent.getInstance().setValue(VIEW_PACKAGE, f_view_package.getText());
        PropertiesComponent.getInstance().setValue(VIEW_INTERFACE_PACKAGE, f_interface_package.getText());
        PropertiesComponent.getInstance().setValue(PRESENTER_PACKAGE, f_presenter_package.getText());
        PropertiesComponent.getInstance().setValue(MAIN_MODULE_NAME, f_main_module_name.getText());
    }

    @Override
    public void reset() {
        f_project_package.setText(Constant.getProjectPackage());
        f_corelibs_package.setText(Constant.getCorelibsPackage());
        f_view_package.setText(Constant.getViewPackage());
        f_interface_package.setText(Constant.getViewInterfacePackage());
        f_presenter_package.setText(Constant.getPresenterPackage());
        f_main_module_name.setText(Constant.getMainModuleName());
    }

    @Override
    public void disposeUIResources() {

    }
}
