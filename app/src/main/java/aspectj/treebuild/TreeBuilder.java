package aspectj.treebuild;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Stack;

/**
 * Created by Sophia on 2017/5/15.
 */
public class TreeBuilder {
    private TreeNode root;
    private String objName;
    private int maxDeep=4;
    class stackdata {
        public Object o;
        public TreeNode node;
        public int deep;
        public stackdata(Object o,TreeNode node,int deep){
            this.o = o;
            this.node = node;
            this.deep=deep;
        }
    }
    private Stack<stackdata> stack;

    public TreeBuilder(String name) {
        root = null;
        objName = name;

        stack = new Stack<stackdata>();
        stack.clear();
    }

    public void run(Object object) {
        root = new TreeNode();

        String name = objName;
        String classType = object.getClass().getSimpleName();
        String value = object.toString();
        root.setData(new Data(classType, name, value));

        if(object!=null)
        {
            stack.push(new stackdata(object,root,0));
            generateTree();
        }
    }

    public void generateTree() {
        while(!stack.empty()) {
            stackdata s = stack.pop();
            Object object = s.o;
            TreeNode parent = s.node;
            Field[] fields = getFields(object);

            if(s.deep >= maxDeep){
                return;
            }

            if(fields == null){
                continue;
            }

            try {
                for (Field f : fields) {
                    if(f == null)
                    {
                        continue;
                    }
                    if(f.getType().getName().equals(parent.getData().getType()))
                    {
                        continue;
                    }
                    Object o = f.get(object);

                    //空类
                    if (o == null) {
                        continue;
                    }

                    if(o.toString().contains("execution"))
                    {
                        continue;
                    }


                   // Log.w("zdp",o.toString());
                    Data temp = generateData(o,f);
                    TreeNode current = new TreeNode();
                    current.setData(temp);
                    current.setParentNode(parent);
                    parent.addChildNode(current);

                    if(isRecursion(current)) {
                        continue;
                    }

                    //成员变量是基本类型
                    if (f.getType().isPrimitive() || f.getType().getSimpleName().equals("String")) {
                        continue;
                    }

                    //成员变量是数组
                    if (o.getClass().isArray()) {
                        for (int i = 0; i < Array.getLength(o); i++) {
                            String elementType = o.getClass().getComponentType().getSimpleName();
                            String elementName = temp.getName()+"["+i+"]";
                            String elementValue = "null";

                            TreeNode elementNode = new TreeNode();
                            elementNode.setData(new Data(elementType,elementName,elementValue));
                            elementNode.setParentNode(current);
                            current.addChildNode(elementNode);

                            Object element = Array.get(o, i);
                            if (element == null) {
                                continue;
                            }else{
                                //generateTree(element, getFields(element), elementNode);
                                stack.push(new stackdata(o,current,s.deep+1));
                            }
                        }
                        continue;
                    }

                    //成员变量是自定义类
                    Class<?> c = Class.forName(f.getType().getName());
                    //Field[] cDeclaredFields = c.getDeclaredFields();
                    //for (Field df : cDeclaredFields) {
                    //    df.setAccessible(true);
                    //}

                    //generateTree(o, cDeclaredFields, current);

                    stack.push(new stackdata(o,current,s.deep+1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private boolean isRecursion(TreeNode node) {
        String type = node.getData().getType();
        TreeNode cur = node.getParentNode();

        if(type.equals(root.getData().getType())) {
            return true;
        }

        while(!cur.toString().equals(root.toString())){
            if(type.equals(cur.getData().getType())) {
                return true;
            }
            cur = cur.getParentNode();
        }

        return false;
    }

    public TreeNode getTree() {
        return root;
    }

    public Field[] getFields(Object obj) {
        Field[] fields = null;
        try {
            //String classType = obj.toString().split("@")[0];

            if(obj.toString().equals("ACTIVITY")){
                return null;
            }

            //Class<?> cla = Class.forName(classType);
            Class<?> cla = obj.getClass();
            if(cla==null)
            {
                return null;
            }
            fields = cla.getDeclaredFields();
            if(fields == null) {
                return null;
            }
            for (Field f : fields) {
                f.setAccessible(true);
            }
        } catch (Exception e) {
            return null;
        } finally {
            return fields;
        }
    }

    public Data generateData(Object object, Field field) {
        String name = field.toString().substring(field.toString().lastIndexOf(".") + 1);
        String type = field.getType().getSimpleName();
        String value = "null";

        if(object == null) {
            return new Data(type, name, value);
        }

        try{
            value = field.get(object).toString();
        }catch(Exception e) {
            value = object.toString();
        }

        return new Data(type, name, value);
    }
}
