package com.ryan;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件Writer, 根据设置里的常量在指定的包下生成类文件.
 * Created by Ryan on 2016/9/6.
 */
public class GeneratorWriter extends WriteCommandAction.Simple {

    private static final String FILE_TYPE = ".java";
    private static final String TYPE_ACTIVITY = "Activity";
    private static final String TYPE_FRAGMENT = "Fragment";
    private static final String TYPE_VIEW = "View";
    private static final String TYPE_PRESENTER = "Presenter";
    private static final String CORELIBS_BASE_PACKAGE = ".base";

    private String fileName;
    /** Activity/Fragment所在的包名 **/
    private String subViewPackage;
    private Project mProject;
    /** 是否是Fragment **/
    private boolean fragment;
    /** 是否是分页 **/
    private boolean paging;

    GeneratorWriter(Project project, String fileName, String subViewPackage, boolean fragment, boolean paging) {
        super(project, "Generate Mvp Classes");

        mProject = project;
        this.fileName = fileName;
        this.subViewPackage = subViewPackage;
        this.fragment = fragment;
        this.paging = paging;
    }

    @Override
    protected void run() throws Throwable {
        addToSvn(writeFile(fileName, TYPE_VIEW, Constant.getViewInterfacePackage(), buildViewContent()));
        if (fragment)
            addToSvn(writeFile(fileName, TYPE_FRAGMENT, Constant.getViewPackage() + "." + subViewPackage, buildActivityOrFragmentContent()));
        else
            addToSvn(writeFile(fileName, TYPE_ACTIVITY, Constant.getViewPackage() + "." + subViewPackage, buildActivityOrFragmentContent()));
        addToSvn(writeFile(fileName, TYPE_PRESENTER, Constant.getPresenterPackage(), buildPresenterContent()));

        // 刷新视图树
        mProject.getBaseDir().refresh(false, true);
    }

    /**
     * 添加文件至svn, 公司原因使用svn管理
     */
    private void addToSvn(File file) {
        if (file == null) return;
        try {
            Runtime.getRuntime().exec("svn add " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据包名生成相应的文件夹, 如C:\IdeaProjects\MvpClassesGenerator\app\src\main\java
     * @param subPackage 子包, 会在android源代码目录下继续生成指定的文件夹
     * @return 生成的文件夹路径
     */
    private String createPackage(String subPackage) {
        String src = mProject.getPresentableUrl() + File.separator + Constant.getMainModuleName()
                + File.separator + "src" + File.separator + "main" + File.separator + "java";
        String[] packages = Constant.getProjectPackage().split("[.]");
        for (String p : packages) {
            src += File.separator + p;
        }

        String[] subPackages = subPackage.split("[.]");
        for (String p : subPackages) {
            src += File.separator + p;
        }

        System.out.println(src);

        File f = new File(src);

        boolean rlt = true;
        if (!f.exists()) rlt = f.mkdirs();

        return rlt ? src : null;
    }

    /**
     * 根据条件生成java文件
     * @param name 文件名
     * @param type 文件类型
     * @param subPackage 指定的子包, 会调用{@link #createPackage}生成相应文件夹
     * @return 生成的文件File对象
     */
    private File createFile(String name, String type, String subPackage) throws IOException {
        String root = createPackage(subPackage);
        if (root == null) return null;

        File file = new File(root + File.separator + name + type + FILE_TYPE);

        boolean rlt = true;
        if (!file.exists()) rlt = file.createNewFile();

        return rlt ? file : null;
    }

    /**
     * 写入文件, 会调用{@link #createFile}生成文件再写入
     */
    private File writeFile(String name, String type, String subPackage, String content) throws IOException {
        File file = createFile(name, type, subPackage);
        if (file == null) return null;

        FileWriter writer = new FileWriter(file);
        writer.write(content);

        writer.close();

        return file;
    }

    /**
     * 拼接View的内容
     */
    private String buildViewContent() {
        String sPackage = Constant.getProjectPackage() + "." + Constant.getViewInterfacePackage();

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));
        builder.append(generateImportCode(Constant.getCorelibsPackage() + CORELIBS_BASE_PACKAGE,
                paging ? "BasePaginationView" : "BaseView"));
        builder.append("\n");

        builder.append("public interface ");
        builder.append(fileName);
        builder.append(TYPE_VIEW);
        if (paging)
            builder.append(" extends BasePaginationView {");
        else
            builder.append(" extends BaseView {");
        builder.append("\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * 拼接Activity/Fragment的内容
     */
    private String buildActivityOrFragmentContent() {
        String tmp;
        if (subViewPackage == null || subViewPackage.length() <= 0) tmp = "";
        else tmp = "." + subViewPackage;
        String sPackage = Constant.getProjectPackage() + "." + Constant.getViewPackage() + tmp;

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));

        builder.append("import android.os.Bundle;\n");
        builder.append(generateImportCode(Constant.getCorelibsPackage() + CORELIBS_BASE_PACKAGE,
                fragment ? "BaseFragment" : "BaseActivity"));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." +
                Constant.getViewInterfacePackage(), fileName + TYPE_VIEW));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." +
                Constant.getPresenterPackage(), fileName + TYPE_PRESENTER));
        builder.append("\n");

        builder.append("public class ");
        builder.append(fileName);
        builder.append(fragment ? TYPE_FRAGMENT : TYPE_ACTIVITY);
        if (fragment)
            builder.append(" extends BaseFragment<");
        else
            builder.append(" extends BaseActivity<");

        builder.append(fileName);
        builder.append(TYPE_VIEW);
        builder.append(", ");
        builder.append(fileName);
        builder.append(TYPE_PRESENTER);

        builder.append("> implements ");
        builder.append(fileName);
        builder.append(TYPE_VIEW);
        builder.append(" {");

        builder.append("\n\n");
        builder.append("    @Override\n");
        builder.append("    protected int getLayoutId() {\n");
        builder.append("        return 0;");
        builder.append("\n    }\n");

        builder.append("\n");
        builder.append("    @Override\n");
        builder.append("    protected void init(Bundle savedInstanceState) {\n");
        builder.append("\n    }\n");

        builder.append("\n");
        builder.append("    @Override\n");
        builder.append("    protected ");
        builder.append(fileName);
        builder.append(TYPE_PRESENTER);
        builder.append(" createPresenter() {\n");
        builder.append("        return new ");
        builder.append(fileName);
        builder.append(TYPE_PRESENTER);
        builder.append("();");
        builder.append("\n    }\n");

        if (paging) {
            builder.append("\n");
            builder.append("    @Override\n");
            builder.append("    public void onLoadingCompleted() {\n");
            builder.append("\n    }\n");

            builder.append("\n");
            builder.append("    @Override\n");
            builder.append("    public void onAllPageLoaded() {\n");
            builder.append("\n    }\n");
        }

        builder.append("}");

        return builder.toString();
    }

    /**
     * 拼接Presenter的内容
     */
    private String buildPresenterContent() {
        String sPackage = Constant.getProjectPackage() + "." + Constant.getPresenterPackage();

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." +
                Constant.getViewInterfacePackage(), fileName + TYPE_VIEW));
        if (paging)
            builder.append(generateImportCode(Constant.getCorelibsPackage() + ".pagination.presenter",
                    "PagePresenter"));
        else
            builder.append(generateImportCode(Constant.getCorelibsPackage() + CORELIBS_BASE_PACKAGE,
                    "BasePresenter"));
        builder.append("\n");

        builder.append("public class ");
        builder.append(fileName);
        builder.append(TYPE_PRESENTER);
        if (paging)
            builder.append(" extends PagePresenter<");
        else
            builder.append(" extends BasePresenter<");
        builder.append(fileName);
        builder.append(TYPE_VIEW);
        builder.append("> {\n");

        builder.append("\n");
        builder.append("    @Override\n");
        builder.append("    public void onStart() {\n");
        builder.append("\n    }\n");

        builder.append("}");

        return builder.toString();
    }

    private String generatePackageCode(String packageName) {
        return "package " + packageName + ";\n\n";
    }

    private String generateImportCode(String packageName, String fileName) {
        return "import " + packageName + "." + fileName + ";\n";
    }

}
