package aspectj;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.ArrayList;

import aspectj.treebuild.TreeBuilder;
import aspectj.treebuild.TreeNode;

/**
 * Created by jason on 2017/5/6.
 */
@Aspect
public class Aspectj {
   static ArrayList<String> strlist=new ArrayList<>();

    private static final String TAG = "aop";

    //com.example.jason.myapplication包下所有类中的所有方法都会被拦截
    @Around("execution(* com.yugy.v2ex.daily.*.*.*(..))")

    public Object onActivityMethodBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName=joinPoint.getSignature().getName();

        Object []obj=joinPoint.getArgs();
        ArrayList<TreeBuilder> primitiveTreeBuilderList=getTrees(obj);
        ArrayList<ArrayList<String>> strlist=new ArrayList<>();

        for(int i=0;i<obj.length;i++)
        {
           strlist.add(CompareValue.getFields(obj[i]));
        }

        Object result=joinPoint.proceed();
        Object []obja=joinPoint.getArgs();
        ArrayList<ArrayList<String>> strlist2=new ArrayList<>();

        for(int i=0;i<obja.length;i++)
        {
            strlist2.add(CompareValue.getFields(obja[i]));
        }
        boolean isChanged=false;
        for(int i=0;i<strlist.size()&&i<strlist2.size();i++)
        {
            if(strlist.get(i)==null&&strlist.get(i)==null)
            {
                continue;
            }
            if(strlist.get(i)==null||strlist.get(i)==null)
            {

                isChanged=true;
                break;
            }
            if(!CompareValue.compare(strlist.get(i),strlist2.get(i)))
            {

                isChanged=true;
                break;
            }
        }
        if(isChanged)
        {
            Log.w("outprint",  methodName+"\n"+strlist+" \n*** \n"+ strlist2+methodName+"\n");
           // printTree(tempobj);
            ArrayList<TreeBuilder> currentTreeList=getTrees(obja);
            printTree(primitiveTreeBuilderList,currentTreeList);
        }

        return result;
    }
    private ArrayList<TreeBuilder> getTrees(Object[] objs){
        ArrayList<TreeBuilder> treeBuilderList=new ArrayList<>();
        for(Object obj:objs){
            if(obj==null){
                continue;
            }
            TreeBuilder treeBuilder = new TreeBuilder(obj.toString());

            treeBuilder.run(obj);
            treeBuilderList.add(treeBuilder);
//         TreeNode tree = getClass.getTree();
//
//         tree.traverse(0);
//
//         Log.w("tree",tree.getSbufAndClear());
        }
        return treeBuilderList;
    }
    private void printTree(ArrayList<TreeBuilder> treeBuilderList1,ArrayList<TreeBuilder> treeBuilderList2)
    {
        for(int i=0;i<treeBuilderList1.size()&&i<treeBuilderList2.size();i++) {
            TreeNode tree1 = treeBuilderList1.get(i).getTree();
            tree1.traverse(0);
           Log.w("tree","运行前\n"+tree1.getSbufAndClear());
            TreeNode tree2 = treeBuilderList2.get(i).getTree();
            tree2.traverse(0);
           Log.w("tree","运行后\n"+tree2.getSbufAndClear());

        }

    }

    //execution(* com.longzhun.UserManager+.*(..)) com.longzhun包下的UserManager类或接口中的所有方法以及子类，实现类的所有方法都会被拦截
}
