import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;


import java.io.*;
import java.util.*;


public class mainTest {

    public static void main(String[] args) throws IOException, ClassHierarchyException, IllegalArgumentException, InvalidClassFileException, CancelException {
        String[] tasks = {"0-CMD","1-ALU","2-DataLog","3-BinaryHeap","4-NextDay","5-MoreTriangle"};
        String type = (args[0].equals("-c"))?"class":(args[0].equals("-m"))?"method":"err";// 判断是方法级别还是类级别
        String projectTarget = args[1];
        String changeInfoPath = args[2];

        Set<String> resMethods = MethodTest.getMethodResult(projectTarget,changeInfoPath);//方法级别的测试
        Set<String> resClass = ClassTest.getClassResult(projectTarget,changeInfoPath);//类级别的测试

        File file;
        Writer out;
        // 类级测试
        if(type.equals("class")){
            Map<String,Set<String>> classMap = ClassTest.getClassMap(projectTarget,changeInfoPath);
//     生成类级别的dot文件并输出
            file = new File(".\\selection-class.txt");
            out = new FileWriter(file);
            for(String s:resClass){
                out.write(s);
                out.write("\n");
            }
            out.close();
        }
        // 方法级测试
        else if(type.equals("method")){
            Map<String,Set<String>> dotMethodMap = MethodTest.getMethodMap(projectTarget,changeInfoPath);
//            生成类级别的dot文件并输出
            file = new File(".\\selection-method.txt");
            out = new FileWriter(file);
            for(String s:resMethods){
                out.write(s);
                out.write("\n");
            }
            out.close();
        }
    }
}
