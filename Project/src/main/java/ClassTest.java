import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassTest {

    public static Set<String> getClassResult(String projectTarget, String changeInfoPath) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {

        String testDirPath = projectTarget + "\\test-classes\\net\\mooctest";//获得测试方法路径
        Set<String> changedClass = new HashSet<String>();
        Set<String> resClass = new HashSet<String>();
        FileReader changeInfoFile = new FileReader(changeInfoPath);
        BufferedReader buffer = new BufferedReader(changeInfoFile);
        String line = null;
        while ((line = buffer.readLine()) != null) {
            changedClass.add(line.split(" ")[0].trim());
        }
        CHACallGraph cg = Util.getGraph(testDirPath);
        for(CGNode node: cg) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if(!Util.isMethodValid(method,".<init>()V"))continue;// 去掉init的情况
                for(CallSiteReference c: method.getCallSites()){
                    String className = c.getDeclaredTarget().getDeclaringClass().getName().toString();
                    if(changedClass.contains(className)) {
                        resClass.add(Util.getMethodFallName(method));
                        break;
                    }
                }
            }
        }
        return resClass;
    }


    public static Map<String,Set<String>> getClassMap(String projectTarget, String changeInfoPath) throws CancelException, ClassHierarchyException, InvalidClassFileException, IOException {
        String srcDirPath = projectTarget + "\\classes\\net\\mooctest";
        String testDirPath = projectTarget + "\\test-classes\\net\\mooctest";
        // 类级映射关系
        Map<String,Set<String>> classMap = new HashMap<String, Set<String>>();

        CHACallGraph cg = Util.getGraph(srcDirPath,testDirPath);
        for(CGNode node: cg) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if(!Util.isMethodValid(method,".<init>()V"))continue;// 去掉init的情况
                if(!method.getSignature().contains("mooctest"))continue;        // 只要包含mooctest的情况
                String methodClassName = method.getDeclaringClass().getName().toString();

                for(CallSiteReference c: method.getCallSites()){
                    String className = c.getDeclaredTarget().getDeclaringClass().getName().toString();
                    if(!className.contains("mooctest")||className.contains("$"))continue;
                    if(classMap.containsKey(className)){
                        classMap.get(className).add(methodClassName);
                    }else{
                        Set<String> addedSet = new HashSet<String>();
                        addedSet.add(methodClassName);
                        classMap.put(className,addedSet);
                    }
                }
            }
        }
        return classMap;
    }
}
