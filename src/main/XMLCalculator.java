package de.itdesign.application;

// java standard DOM libraries for parsing XML documents (Java SE: https://docs.oracle.com/javase/7/docs/api/org/w3c/dom/package-summary.html)

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XMLCalculator {

    public static void main(String[] args) {

        //Don't change this part
        if (args.length == 3) {
            //Path to the data file, e.g. data/data.xml
            final String DATA_FILE = args[0];
            //Path to the data file, e.g. operations/operations.xml
            final String OPERATIONS_FILE = args[1];
            //Path to the output file
            final String OUTPUT_FILE = args[2];
            // execute method dynamicalDataProcessing(String pathToDataFile, String pathToOperationsFile, String pathToOutputFolder)
            // with the paths to the 'data.xml', 'operations.xml' and output folder for the 'output.xml' file as input parameters
            dynamicalDataProcessing(DATA_FILE, OPERATIONS_FILE, OUTPUT_FILE);
        } else {
            // optionally:
            // execute method 'dynamicalDataProcessing()' without input parameters
            // the paths to the 'data.xml', 'operations.xml' and 'output.xml' file are going to be entered via console input
//            dynamicalDataProcessing();
            System.exit(1);
        }
    }


    // 1st 'dynamicalDataProcessing'-method. with input parameters. Here the links to the 'data.xml' and 'operations.xml' files
    // and the output path are parameter inputs.
    // This method reads in the files, calls all other methods to perform the operations and writes the output.xml file to the output path
    public static void dynamicalDataProcessing(String pathToDataFile, String pathToOperationsFile, String pathToOutputFile) {

        // read in the data.xml file and store it in a variable
        File dataFile = new File(pathToDataFile);
        // read in the operations.xml file and store it in a variable
        File operationsFile = new File(pathToOperationsFile);

        // call performDataCalculation to perform all operations in the operation.xml file on the data.xml file
        // and assign the results to a list of tuples, ech containing the operation name and the operation result
        ArrayList<TupleOut> elementValuesList = performDataCalculation(dataFile, operationsFile);
        // write an output XML file based on the list of output tuples to the output folder
        createOutputXMLDoc(pathToOutputFile, elementValuesList);
    }


    // 2nd 'dynamicalDataProcessing'-method without input parameters. Here the paths to the files and output folder are entered via the console input
    // This method reads in the files, calls all other methods to perform the operations and writes the output.xml file to the output path
    public static void dynamicalDataProcessing() {

        // create a scanner to be able to read the links to the 'data.xml' file, the 'operations.xml' file and the output folder
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the path to your 'data.xml' file:\r");
        String pathToDataFile = scanner.next();
        System.out.println("Please enter the path to your 'operations.xml' file:\r");
        String pathToOperationsFile = scanner.next();
        System.out.println("Please enter the path to your output file:\r");
        String pathToOutputFile = scanner.next();
        System.out.println("The path to your 'data.xml' and 'operations.xml' file was set. \n" +
                "Process started. An output file with the results of our operations will be saved to your output path ...\r");

        // read in the data.xml file and store it in a variable
        File dataFile = new File(pathToDataFile);
        // read in the operations.xml file and store it in a variable
        File operationsFile = new File(pathToOperationsFile);

        // call performDataCalculation to perform all operations in the operation.xml file on the data.xml file
        // and assign the results to a list of tuples, ech containing the operation name and the operation result
        ArrayList<TupleOut> elementValuesList = performDataCalculation(dataFile, operationsFile);
        // write an 'output.xml' file based on the list of output tuples to the output folder
        createOutputXMLDoc(pathToOutputFile, elementValuesList);
    }


    // method to perform the calculations:
    // - parses the 'operation.xml' file
    // - loops through all single operations
    // - calls performOperation() to perform the operations on the 'data.xml' file
    // - returns a list of operation output results (operationOutputTupleList)
    public static ArrayList<TupleOut> performDataCalculation(File dataFile, File operationsFile) {
        // define an empty list for output tuples. Here, each operation result can be stored with the regarding operation name
        ArrayList<TupleOut> operationsOutputTupleList = new ArrayList<>();
        // define a variable to store ech operation result (type double) while looping through the different operations
        double resultOperationDouble;
        // define a variable to store ech operation result (type String) while looping through the different operations
        String resultOperationString;
        // create a DocumentBuilderFactory to be able to parse a .xml document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        //
        try {
            // create a DocumentBuilder
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // parse the input file 'operations.xml' and store in the variable 'operations'
            Document operations = dBuilder.parse(operationsFile);
            // normalize the XML input file
            operations.getDocumentElement().normalize();
            // get the operation from the file
            NodeList operationNodesList = operations.getElementsByTagName("operation");

            // loop through all operation elements (operations)
            for (int tempOperation = 0; tempOperation < operationNodesList.getLength(); tempOperation++) {
                // store the current operation in a variable
                Node operationNode = operationNodesList.item(tempOperation);

                // if the operation in the list is an element node,
                // - get the operation details (name, type, func, attrib, filter)
                // - call the 'performOperation' method to perform the Operation on the 'data.xml' file
                // - convert the result (type double) to type String
                // - add the operation result to the list of operations results
                if (operationNode.getNodeType() == Node.ELEMENT_NODE) {
                    // get the element of the current operation node
                    Element operationElement = (Element) operationNode;

                    // store the operation details (name, type, func, attrib and filter) in corresponding variables
                    String operationName = operationElement.getAttribute("name");
                    String operationType = operationElement.getAttribute("type");
                    String operationFunc = operationElement.getAttribute("func");
                    String operationAttrib = operationElement.getAttribute("attrib");
                    String operationFilter = operationElement.getAttribute("filter");

                    // useful console output
//                    System.out.println("-------------");
//                    System.out.println("name= " + operationName);
//                    System.out.println("attrib= " + operationAttrib);
//                    System.out.println("filter= " + operationFilter);

                    // call the 'performOperation' method to perform the current operation on the 'data.xml' file
                    resultOperationDouble = performOperation(dataFile, operationName, operationType, operationFunc, operationAttrib, operationFilter);

                    // convert results (double) to String AND with precision of two decimal places
                    // (as shown in the sample output.xml file, the output 0.0 is represented as 0.00)
                    resultOperationString = convertDoubleToString2Decimal(resultOperationDouble);
//                    System.out.println(resultOperationString);
                    // add the result to the list of operation output results (operationOutputTupleList)
                    operationsOutputTupleList.add(new TupleOut(operationName, resultOperationString));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        // return the list of operation output results (operationOutputTupleList) containing the output tuples of all operations
        return operationsOutputTupleList;
    }


    // method to performing the single operations on the data.xml file:
    // - parses the data.xml file,
    // - filter the city Elements for matches
    // - perform the calculation,
    // - returns the operation result (double)
    public static double performOperation(File dataFile, String name, String type, String func, String attrib, String filter) {
        // initialise a variable to store the element value of a city element (type String)
        String elementValueString = "";
        // initialise a variable to store the element value of a city element (type double)
        double elementValueDouble;
        // initialise a list to store the element values of the matched cities
        ArrayList<Double> elementValuesList = new ArrayList<>();
        // initialise a variable to store the result of the operation
        double resultOperation;
        // define a pattern of the String (regular expression) stored in 'filter' which is used in a search
        Pattern pattern = Pattern.compile(filter);
        // generate the error output info String
        String errorInfo = ">>> operation information: name= " + name + ", attrib= " + attrib + ", filter= " + filter + ", type= " + type + ", cityName= ";

                // create a DocumentBuilderFactory to be able to parse a .xml document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            // create a DocumentBuilder
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // parse the input file 'data.xml' and store in the variable 'data'
            Document data = dBuilder.parse(dataFile);
            // normalize the XML input file
            data.getDocumentElement().normalize();
            // get the city nodes from the input file:
            NodeList cityNodesList = data.getElementsByTagName("city");

            // loop through the city elements (cities)
            // if the city in the list is an element node:
            // - check if the city name matches the filter pattern
            // - get the city attribute value (String) and convert it to a double
            // - add the city attribute value to the list of city attribute values
            // - add the operation result to the list of operations results
            for (int tempCity = 0; tempCity < cityNodesList.getLength(); tempCity++) {
                // get the current city node
                Node cityNode = cityNodesList.item(tempCity);
                // check if cityNode is of type node
                if (cityNode.getNodeType() == Node.ELEMENT_NODE) {
                    // get the element of the current city node
                    Element cityElement = (Element) cityNode;

                    // store the city name in a variable to test whether it matches the filter pattern (regular expression)
                    Matcher matcher = pattern.matcher(cityElement.getAttribute("name"));
                    // store in a boolean variable whether the city name matched the filter pattern (regular expression)
                    boolean matchFound = matcher.find();

                    // if the city named matches a filter pattern (regular expression):
                    if (matchFound) {
                        try {
                            // if the requested attribute is an element attribute:
                            if (type.equals("attrib")) {
                                // get the selected attribute data from the regarding city element
                                elementValueString = cityElement.getAttribute(attrib);

                            // if the requested attribute is child node:
                            } else if (type.equals("sub")) {
                                // get the selected attribute data from the regarding city element
                                elementValueString = cityElement.getElementsByTagName(attrib).item(0).getTextContent();

                            // if the 'type' in the operations.xml file was set to an invalid value print to the console that the operation could not be conducted
                            } else {
                                System.out.println("ERROR!: In operation '" + name + "': the attribute 'type' was set to an invalid value. Expected values: 'attrib' or 'sub'. " +
                                        "No calculation could be conducted for operation '" + name + "'. The result value in the output file was set to 0.");
                                // break out of the loop
                                break;
                            }

                            // convert the loaded Number (type String) to type double.
                            elementValueDouble = Double.parseDouble(elementValueString);
                            // add the city attribute value to the list of city attribute values
                            elementValuesList.add(elementValueDouble);

                        // if the searched child node or attribute was not present or had invalid entries (e.g. empty or integers) in the given city element:
                            // - print this information to the console
                            // - exclude the current city element from the calculation of this operation
                            // - continue with the next city element
                        } catch (NullPointerException e) {
                            // child node not existent
                            System.out.println("----------\nERROR!: In operation '" + name + "': For the city of '" + cityElement.getAttribute("name") + "', no valid child node '" + attrib
                                    + "' was found. The city of '" + cityElement.getAttribute("name") + "' was excluded from the calculation of this operation.\n"
                                    + errorInfo + cityElement.getAttribute("name"));
                            // attribute or child node text content is not existent or invalid
                        } catch (NumberFormatException e) {
                            String attrVal = "";
                            // if the searched 'attrib' in operation.xml refers to an attribute (see operation type). Insert 'attribute' in the console output message
                            if (type.equals("attrib"))
                            {
                                attrVal = "attribute";
                            // if the searched 'attrib' in operation.xml refers to a child node (see operation type). Insert 'child node' in the console output message
                            } else if (type.equals("sub")){
                                attrVal = "child node";
                            }
                            // console output message in the case the attribute or child node text content i not existent or invalid
                            System.out.println("----------\nERROR!: In operation '" + name + "': For the city of '" + cityElement.getAttribute("name") + "', no valid " + attrVal + " of name '" + attrib
                                    + "' was found. Digits were expected as " + attrVal + " text content. The city of '" + cityElement.getAttribute("name") + "' was excluded from the calculation of this operation.\n"
                                    + errorInfo + cityElement.getAttribute("name"));
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        // if the list of city attribute values contains values:
        if (!elementValuesList.isEmpty()) {
            // call performFunc to perform the calculation of operation attribute 'func" on the list elements and return the result
            resultOperation = performFunc(elementValuesList, func);
            return resultOperation;
        } else {
            // if the list of city attribute values is empty (e.g. due to invalid type attribute in operations.xml) return 0 as result
            return 0;
        }
    }


    public static double performFunc(ArrayList<Double> list, String func) {
//      initialise a variable to store the result of the calculation
        double resultFunc;

        // depending on wich calculation should be performed (see 'func'):
        // - call the corresponding method or directly calculate the result
        try {
            switch (func) {
                case "sum":
                    resultFunc = sumList(list);
                    break;
                case "average":
                    resultFunc = averageList(list);
                    break;
                case "min":
                    resultFunc = minList(list);
                    break;
                case "max":
                    resultFunc = maxList(list);
                    break;
                default:
                    // if an unknown function was set in the 'operations.xml' file, throw an IllegalStatException
                    String type = "-----\nThe requested function '" + func + "' has not been implemented yet.";
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            // if an unknown function was set in the 'operations.xml' file:
            // - Print out to console that this function has not been implemented yet and return 0 as result for this operation.
        } catch (IllegalStateException e) {
            System.out.println("-----\nERROR!: The requested function '" + func + "' has not been implemented yet. The return value for this operation was set to 0.");
            return 0;
        }
        // set the result of the function to a precision of two decimal places and return the value
//        resultFunc =  ((long)(Math.round(resultFunc * 100))/100.0);
        return resultFunc;
    }


    // method to sum all element entries in a list of doubles
    public static double sumList(ArrayList<Double> list) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }

    // method to find the average of all values in a list of doubles
    public static double averageList(ArrayList<Double> list) {
        return sumList(list) / list.size();
    }

    // method to find the minimum value in a list of doubles
    // set the minimum in the beginning to the maximum possible value of type double
    public static double minList(ArrayList<Double> list) {
        double min = Double.MAX_VALUE;
        // loop over all list elements and compare every list entry to the current 'min' value
        for (int i = 0; i < list.size(); i++) {
            // if the current list entry is smaller than the current minimum: update min and store the current list entry in variable 'min'
            if (list.get(i) < min) {
                min = list.get(i);
            }
        }
        return min;
    }

    // method to find the maximum value in a list of doubles
    public static double maxList(ArrayList<Double> list) {
        // set the maximum in the beginning to the minimum possible value of type double
        double max = Double.MIN_VALUE;
        // loop over all list elements and compare every list entry to the current 'max' value
        for (int i = 0; i < list.size(); i++) {
            // if the current list entry is bigger than the current maximum: update min and store the current list entry in variable 'max'
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }


    // method to convert double to String with a precision of exactly two decimal places (0.0 is represented as 0.00)
    public static String convertDoubleToString2Decimal(double number) {
        // multiply by 100 and round. ATTENTION: Math.round Doesn't round upwards in the case of exactly #.##5 (false internal representation when multiplying by 100: (#.##5 * 100) results in #.4999999999999999)
        double numberE2round = Math.round(number * 100);
        // cast to long to get rid of decimals, divide by 100 to get the number back to the original power of ten,
        // gain a double (to be able to represent the decimals) and convert it to a String
        String numberString2dec = Double.toString(((long) (numberE2round)) / 100.0);
        // if modulo 100 or modulo 10 of rounded (number*100) returns a remainder of 0, add a '0' at the end of the obtained number (String) to obtain exactly 2 decimal places
        if (((numberE2round % 100) == 0) | ((numberE2round % 10) == 0)) {
            numberString2dec = numberString2dec + "0";
        }
        return numberString2dec;
    }


    // method to write XML files
    public static void createOutputXMLDoc(String pathToOutputFile, ArrayList<TupleOut> resultTuplesList) {
        // create a DocumentBuilderFactory
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            // create a DocumentBuilder
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // parse the XML input file: create XML document
            Document outputDoc = dBuilder.newDocument();
            // create root element
            Element rootElement = outputDoc.createElement("results");
            // set the root element
            outputDoc.appendChild(rootElement);

            // loop over all conducted operations in the list of output tuples:
            for (int i=0; i < resultTuplesList.size(); i++) {
                //  create a result elements
                Element result = outputDoc.createElement("result");
                // append the 'result' Element as child node to the root Element
                rootElement.appendChild(result);
                // set the operation name as attribute
                result.setAttribute("name", resultTuplesList.get(i).getName());
                // set the operation result as text content
                result.setTextContent(resultTuplesList.get(i).getResult());
            }

            // write dom document to a file:
            // generate an output stream based on the path to output.xml file
            try (FileOutputStream outputLink = new FileOutputStream(pathToOutputFile)) {
                // call method writeXML to write the 'output.xml' document to the output folder
                writeXml(outputDoc, outputLink);
                // print to console that everything worked well + the path to the output.xml file
                System.out.println("--------------------\n"
                        + "End: The process ended successfully. Here is the path to the output file:\n"
                        + pathToOutputFile);
            } catch (IOException | TransformerException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    // method to write the 'output.xml' document to the output folder
    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        // create a TransformerFactory
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // create a new transformer
        Transformer transformer = transformerFactory.newTransformer();
        // pretty print XML (with indentation)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // get the document
        DOMSource source = new DOMSource(doc);
        // get the output stream
        StreamResult result = new StreamResult(output);
        // transform the document to the output folder
        transformer.transform(source, result);
    }
}


//  Class for storing the all the relevant output information (operation name, operation result) of each operation wrapped together
class TupleOut {

    // fields
    private String name;
    private String result;

    // constructor
    public TupleOut(String name, String result) {
        this.name = name;
        this.result = result;
    }

    // getter
    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
