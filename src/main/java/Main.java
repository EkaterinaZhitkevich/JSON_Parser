
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameJson = "data.json";
        String fileNameXML = "data.xml";

        List<Employee> employees1 = parseCSV(columnMapping, fileNameCsv);
        String json1 = listToJson(employees1);
        createJsonFile(fileNameJson, json1);
        List<Employee> employees2 = parseXML(fileNameXML);

        String json2 = listToJson(employees2);
        createJsonFile(fileNameJson, json2);

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).withMappingStrategy(strategy).build();
            list = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node root = document.getDocumentElement();
            readXML(root, employees);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
      return employees;
    }

    public static void readXML(Node node, List<Employee> list) {
        NodeList nodeList = node.getChildNodes();
        List<String> strings = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
        Node nodeTemp = nodeList.item(i);
        if (Node.ELEMENT_NODE == nodeTemp.getNodeType()){
            Element element = (Element) nodeTemp;
            String name = element.getNodeName();
            String value = element.getTextContent();
            Employee employee = new Employee();
            switch (name){
                case "id":
                    employee.id = Long.parseLong(value);
                    break;
                case "firstName":
                    employee.firstName = value;
                    break;
                case "lastName":
                    employee.lastName = value;
                    break;
                case "country":
                  employee.country = value;
                  break;
                case "age":
                    employee.age= Integer.parseInt(value);
                    break;
                default:
                    break;
            }
            list.add(employee);
            readXML(nodeTemp, list);
        }
        }
    }

    public static void createJsonFile(String fileName, String json) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createCSV(String[]... persons) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            for (int i = 0; i < persons.length; i++) {
                writer.writeNext(persons[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
