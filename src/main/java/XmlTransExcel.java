
import common.ExcelProperties;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class XmlTransExcel {

    /**
     * getTargetElement
     * @param root root
     * @param targetName 目标结构名称
     * @return Element
     */
    private static Element getTargetElement(Element root,String targetName){
        if (root.getName().equals(targetName)){
            return root;
        }
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()){
            getTargetElement(iterator.next(),targetName);
        }
        return null;
    }
    /**
     * 按管理版本名称输出版本号
     * @param xmlName xml文件名
     * @param excelName excel文件名
     */
    public static void getDependency(String xmlName,String excelName,String conditional){

        Document document = xmlLoad(ExcelProperties.getXmlFilePath(xmlName));
        Element root = document.getRootElement();
        List<String> rootList = ExcelProperties.getXmlMatchNode(xmlName);

        //获取所需要xml文件匹配条件的父节点
        assert rootList != null;
        Element masterElement1 = getTargetElement(root, rootList.get(0));
        Element masterElement2 = getTargetElement(root, rootList.get(1));

        //迭代输出按管理版本名称输出版本号
        XSSFWorkbook workbook = null;
        try {

            //设置excel列名
            workbook = new XSSFWorkbook();
            XSSFSheet sheetAt = workbook.createSheet();
            Row titleRow = sheetAt.createRow(0);

            //设置列名
            List<String> columnNameList = ExcelProperties.getExcelColumnName(excelName);
            if (columnNameList != null){
                for (int i=0; i<ExcelProperties.getExcelColumnSize(excelName); i++){
                    titleRow.createCell(i).setCellValue(columnNameList.get(i));
                }
            }else{
                throw new RuntimeException(excelName + "列名不存在");
            }

            //迭代
            int Count = 1;
            assert masterElement1 != null;
            Iterator<Element> depIterator = masterElement1.elementIterator();
            while (depIterator.hasNext()){

                //获取第一个匹配孙节点
                Iterator<Element> tempIterator = depIterator.next().elementIterator();
                Element depTarget = null;
                while (tempIterator.hasNext()){
                    Element tempElement = tempIterator.next();
                    if (tempElement.getName().indexOf(Objects.requireNonNull(ExcelProperties.getXmlConditional(conditional))) > 0){
                        depTarget = tempElement;
                    }
                }
                if (depTarget != null && depTarget.getName().length() > 0){
                    String depVerValue = depTarget.getText().substring(2, depTarget.getText().length() - 1);

                    //依赖jar包版本
                    assert masterElement2 != null;
                    Iterator<Element> verIterator = masterElement2.elementIterator();
                    while (verIterator.hasNext()){

                        //dependencyManagement
                        Element verChildElm = verIterator.next();
                        String manVersion = verChildElm.getName();
                        if (manVersion.equals(depVerValue)) {

                            //存储每个单元格的值
                            List<String> columnValue = new ArrayList<>();
                            for (String s : columnNameList) {
                                String value = depTarget.element(s).getText();
                                columnValue.add(value);
                            }

                            //调用poi按excel格式输出
                            Row row = sheetAt.createRow(Count);
                            List<String> rowNameList = ExcelProperties.getExcelRowName(excelName);

                            //判断是否有行标题
                            int start = 0;
                            Integer columnSize = ExcelProperties.getExcelRowSize(xmlName);
                            if (rowNameList != null && rowNameList.size() > 0 ){
                                row.createCell(start).setCellValue(rowNameList.get(start));
                                columnSize++;
                            }

                            //循环输出到excel单元格中
                            for (int i = start; i < columnSize; i++){
                                row.createCell(i).setCellValue(columnValue.get(i));
                            }
                            Count++;
                            masterElement2.remove(verChildElm);
                        }
                    }
                }
            }
            try (OutputStream fileOut = new FileOutputStream(Objects.requireNonNull(ExcelProperties.getExcelFilePath(excelName)))) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try{
                if (workbook != null){
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param filename xml文件
     */
    public static Document xmlLoad(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(filename)); // 读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }
}
