import com.intellij.ide.util.PropertiesComponent;

/**
 * 设置中的一些常量
 * Created by Ryan on 2016/9/6.
 */
public class Constant {
    public static final String DEFAULT_PROJECT_PACKAGE = "com.bm";
    public static final String DEFAULT_CORELIBS_PACKAGE = "com.corelibs.base";
    public static final String DEFAULT_VIEW_PACKAGE = "view";
    public static final String DEFAULT_VIEW_INTERFACE_PACKAGE = "view.interfaces";
    public static final String DEFAULT_PRESENTER_PACKAGE = "presenter";
    public static final String DEFAULT_MAIN_MODULE_NAME = "app";

    public static String getProjectPackage() {
        return PropertiesComponent.getInstance().getValue(Settings.PROJECT_PACKAGE, DEFAULT_PROJECT_PACKAGE);
    }

    public static String getCorelibsPackage() {
        return PropertiesComponent.getInstance().getValue(Settings.CORELIBS_PACKAGE, DEFAULT_CORELIBS_PACKAGE);
    }

    public static String getViewPackage() {
        return PropertiesComponent.getInstance().getValue(Settings.VIEW_PACKAGE, DEFAULT_VIEW_PACKAGE);
    }

    public static String getViewInterfacePackage() {
        return PropertiesComponent.getInstance().getValue(Settings.VIEW_INTERFACE_PACKAGE, DEFAULT_VIEW_INTERFACE_PACKAGE);
    }

    public static String getPresenterPackage() {
        return PropertiesComponent.getInstance().getValue(Settings.PRESENTER_PACKAGE, DEFAULT_PRESENTER_PACKAGE);
    }

    public static String getMainModuleName() {
        return PropertiesComponent.getInstance().getValue(Settings.MAIN_MODULE_NAME, DEFAULT_MAIN_MODULE_NAME);
    }
}
