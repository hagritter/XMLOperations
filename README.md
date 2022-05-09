# Java Challenge
## The XML calculation Problem:

Create a tool that processes a data and an operation XML file. As output it writes the result in a new output XML file.
The sample files ("data.xml", "operations.xml" and "output.xml") provide the basic schema.

Format of the input data.xml file: 
1) It has got a top-level element called "data".
2) The top-level element "data" has got child elements, all with the same name "city".
3) The "city" elements can have a variable number of attributes and child nodes.
4) Each "city" element has got at least an attribute called "name" which is used for filtering.

Format of the operation.xml file:
1) It has got a top-level element called "operations"
2) The top-level element "operations" has got child elements, all with the same name "operation"
3) Each "operation" element has got the following attributes:

- "name": the name how this operation is called, it is also used for the output
- "attrib": the name of the attribute or child node which will be evaluated by the operation
- "type": gives the information if the name in "attrib" refers to an attribute or a child node
- "func": indicates the function which will be performed on the matched entries. Possible functions are: "min", "max", "sum", or "average".
- "filter": here a regular expression is used to filter the city elements. All city names that match the regular expression will be included in the calculation of the operation
.
Format of the output.xml file:
1) It has got a top-level element called "results".
2) The top-level element "results" has got child elements, all with the same name "result".
3) Each "result" element has got an attribut "name". The text content of the attribute "name" is the same as the "name" entry of the corresponding operation.
4) The text content of each "result" element represents the calcuated result of the corresponding operation. The results should have a precision of exactly two decimal places.

General things:
- All standard libraries are allowed

## Example files:
- data.xml - sample input data file
- operations.xml – Sample for operations to be calculated
- output.xml – The result for the  delivered sample files

