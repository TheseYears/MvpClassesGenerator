import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratorWriter extends WriteCommandAction.Simple {

    private static final String FILE_TYPE = ".java";
    private static final String TYPE_ACTIVITY = "Activity";
    private static final String TYPE_FRAGMENT = "Fragment";
    private static final String TYPE_VIEW = "View";
    private static final String TYPE_PRESENTER = "Presenter";

    private String fileName, subViewPackage;
    private Project mProject;
    private boolean fragment, paging;

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

        mProject.getBaseDir().refresh(false, true);
    }

    private void addToSvn(File file) {
        if (file == null) return;
        try {
            Runtime.getRuntime().exec("svn add " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private File createFile(String name, String type, String subPackage) throws IOException {
        String root = createPackage(subPackage);
        if (root == null) return null;

        File file = new File(root + File.separator + name + type + FILE_TYPE);

        boolean rlt = true;
        if (!file.exists()) rlt = file.createNewFile();

        return rlt ? file : null;
    }

    private File writeFile(String name, String type, String subPackage, String content) throws IOException {
        File file = createFile(name, type, subPackage);
        if (file == null) return null;

        FileWriter writer = new FileWriter(file);
        writer.write(content);

        writer.close();

        return file;
    }

    private String buildViewContent() {
        String sPackage = Constant.getProjectPackage() + "." + Constant.getViewInterfacePackage();

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));
        builder.append(generateImportCode(Constant.getCorelibsPackage(), paging ? "BasePaginationView" : "BaseView"));
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

    private String buildActivityOrFragmentContent() {
        String sPackage = Constant.getProjectPackage() + "." + Constant.getViewPackage() + "." + subViewPackage;

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));

        builder.append("import android.os.Bundle;\n");
        builder.append(generateImportCode(Constant.getCorelibsPackage(), fragment ? "BaseFragment" : "BaseActivity"));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." + Constant.getViewInterfacePackage(), fileName + TYPE_VIEW));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." + Constant.getPresenterPackage(), fileName + TYPE_PRESENTER));
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
        builder.append("    @Override protected int getLayoutId() {\n");
        builder.append("        return 0;");
        builder.append("\n    }\n");

        builder.append("\n");
        builder.append("    @Override protected void init(Bundle savedInstanceState) {\n");
        builder.append("\n    }\n");

        builder.append("\n");
        builder.append("    @Override protected ");
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
            builder.append("    @Override public void onLoadingCompleted() {\n");
            builder.append("\n    }\n");

            builder.append("\n");
            builder.append("    @Override public void onAllPageLoaded() {\n");
            builder.append("\n    }\n");
        }

        builder.append("}");

        return builder.toString();
    }

    private String buildPresenterContent() {
        String sPackage = Constant.getProjectPackage() + "." + Constant.getPresenterPackage();

        StringBuilder builder = new StringBuilder();
        builder.append(generatePackageCode(sPackage));
        builder.append(generateImportCode(Constant.getProjectPackage() + "." + Constant.getViewInterfacePackage(), fileName + TYPE_VIEW));
        builder.append(generateImportCode(Constant.getCorelibsPackage(), paging ? "BasePaginationPresenter" : "BasePresenter"));
        builder.append("\n");

        builder.append("public class ");
        builder.append(fileName);
        builder.append(TYPE_PRESENTER);
        if (paging)
            builder.append(" extends BasePaginationPresenter<");
        else
            builder.append(" extends BasePresenter<");
        builder.append(fileName);
        builder.append(TYPE_VIEW);
        builder.append("> {\n");

        builder.append("\n");
        builder.append("    @Override public void onStart() {\n");
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
