package com.geb.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMapper {

    private Logger log =LoggerFactory.getLogger(getClass());
    
    private final ClassLoader classLoader = getClass().getClassLoader();
    //xml文件所在的目录
    private static final String fileName = "mybatis";
    //entity实体类所在的目录
    private static final String packageName = "com.geb.entity";
    private static final String packageFileStr = "com/geb/entity";
    private static List<Class<?>> entitys = new ArrayList<Class<?>>();

    private static SAXReader reader = new SAXReader();

    private static Map<String, String> javaToSql = new HashMap();

    //AUTOMAPPER
    public void domMappers() throws Exception {
        initJavaToSql();
        LinkedList<File> list = getMappers();
        File packageFile = new File(classLoader.getResource(packageFileStr).getFile());
        findAndAddClassesInPackageByFile(packageName, URLDecoder.decode(packageFile.toString(), "UTF-8"), true, entitys);
        for (int i = 0; i < list.size(); i++) {
            System.out.print("\n***********************************\n开始" + list.get(i).toString() + "的自动化映射，并动态加载class\n");
            Document document = reader.read(list.get(i));
            Element root = document.getRootElement();
            Element resultMap = root.element("resultMap");
            resultMap.clearContent();
            Attribute type = resultMap.attribute("type");
            Class<?> entity = Class.forName(type.getValue());
            //获得该实体类的所有属性
            Field[] fields = entity.getDeclaredFields();
            addDom(fields, resultMap);

            //添加其他方便使用的res
            Element resultMap_string = root.addElement("resultMap");
            resultMap_string.addAttribute("id", "string");
            resultMap_string.addAttribute("type", String.class.toString().replace("class", "").replace(" ", ""));
            isHas(root, resultMap_string);
            Element resultMap_int = root.addElement("resultMap");
            resultMap_int.addAttribute("id", "int");
            resultMap_int.addAttribute("type", Integer.class.toString().replace("class", "").replace(" ", ""));
            isHas(root, resultMap_int);
            System.out.print("SUCCESS:" + list.get(i).toString() + "的自动化映射结束,可使用的resultMap<map,int,string>\n");
            saveDocument(document, list.get(i).toString(), "UTF-8");
        }
    }

    //是否存在 如果存在则删除
    public void isHas(Element root, Element children) {
        List<Attribute> listAttr = children.attributes();
        String xpath = "";
        for (Attribute attr : listAttr) {
            xpath += "[@" + attr.getName() + "=" + "'" + attr.getValue() + "']";
        }
        if (root.selectNodes(children.getName() + xpath) != null && root.selectNodes(children.getName() + xpath).size() > 1) {
            root.remove(children);
        }
    }

    static void initJavaToSql() {
        javaToSql.put(Integer.class.toString(), "INTEGER");
        javaToSql.put("int", "INTEGER");
        javaToSql.put(String.class.toString(), "VARCHAR");
        javaToSql.put(Timestamp.class.toString(), "TIMESTAMP");
        javaToSql.put(boolean.class.toString(), "tinyint");
        javaToSql.put(Boolean.class.toString(), "tinyint");
    }

    //获取所有mybatis的xml文件
    public LinkedList<File> getMappers() {
        LinkedList<File> list = new LinkedList<>();
        File file = new File(classLoader.getResource(fileName).getFile());
        File[] mappers = file.listFiles();
        for (int i = 0; i < mappers.length; i++) {
            list.add(mappers[i]);
        }
        return list;
    }


    //以文件的形式来获取包下的所有Class,深度循环
    public void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        //获取此包的目录 建立一个File  
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回  
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录  
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件  
        for (File file : dirfiles) {
            //如果是目录 则继续扫描  
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去  
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addDom(Field[] fields, Element e) {
        for (Field field : fields) {
            //如果这个属性的类型是一个写的实体类
            if (entitys.contains(field.getType())) {
                System.out.print("#发现《一对一》关系，对应实体类:" + field.getType().toString().replace("class", "") + "\n");
                Element associationElement = e.addElement("association");
                associationElement.addAttribute("property", field.getName());
                associationElement.addAttribute("javaType", field.getType().toString().replace("class", "").replace(" ", ""));
                isHas(e, associationElement);
                Class<?> childrenEntity = field.getType();
                Field[] childrenFields = childrenEntity.getDeclaredFields();
                addDom(childrenFields, associationElement);
            } else if (field.getGenericType().toString().equals("List") || field.getGenericType().toString().equals("ArrayList")) {
                System.out.print("#发现《一对多》关系，对应实体类:" + field.getType().toString().replace("class", "").replace(" ", "") + "\n");
                Element collectionElement = e.addElement("collection");
                collectionElement.addAttribute("property", field.getName());
                collectionElement.addAttribute("ofType", field.getType().toString().replace("class", "").replace(" ", ""));
                isHas(e, collectionElement);
                Class<?> childrenEntity = field.getType();
                Field[] childrenFields = childrenEntity.getDeclaredFields();
                addDom(childrenFields, collectionElement);
            } else {
                System.out.print("#添加对应属性名：" + field.getName() + "\n");
                String jdbcType = javaToSql.get(field.getGenericType().toString());
                String property = field.getName();
                StringBuffer column = new StringBuffer();;
                //为resultMap添加节点
                Element resultElement;
                if (property.equals("id")) {
                    resultElement = e.addElement("id");
                } else {
                    resultElement = e.addElement("result");
                }
                resultElement.addAttribute("property", property);
                //col属性要判断类属性中有没有大写的，有就加_
                char[] ch = property.toCharArray();
                for (int a = 0; a < ch.length; a++) {
                    if (ch[a] >= 'A' && ch[a] <= 'Z') {
                        column.append('_');
                        column.append(Character.toLowerCase(ch[a]));
                    } else {
                        column.append(ch[a]);
                    }
                }
                resultElement.addAttribute("column", column.toString());
                resultElement.addAttribute("jdbcType", jdbcType);
                isHas(e, resultElement);
            }
        }
    }

    public  void saveDocument(Document doc, String filePath, String encoding) {
        try {
            OutputFormat fmt = OutputFormat.createPrettyPrint();
            fmt.setEncoding(encoding);
            XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding), fmt);
            xmlWriter.write(doc);
            xmlWriter.close();
            System.out.print("保存修改后的" + filePath + "成功\n***********************************\n");
        } catch (Exception e) {
            log.error("ERRO:保存修改后的" + filePath + "发生异常");
        }
    }

}
