/**
 * 
 */
//package org.biomart.processors;
package org.queryall.mapping;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.biomart.common.constants.OutputConstants;
import org.biomart.processors.ProcessorImpl;
import org.biomart.processors.annotations.ContentType;
import org.biomart.queryEngine.Query;
import org.biomart.queryEngine.QueryElement;
import org.biomart.queryEngine.QueryElementType;


/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
//@ProcessorName("RDFXML")
@ContentType("application/rdf+xml")
public class RDFXML extends ProcessorImpl 
{
    @Override
    public void beforeQuery(Query query, OutputStream out) throws IOException 
    {
        String prelude = null;
        List<String> variableNames = new LinkedList<String>();
        String exception = null;

        // Get all of the variable names
        // TODO: is this the correct way to fetch the attribute names???
        for(QueryElement nextQueryElement : query.getOriginalAttributeOrder())
        {
        	if(nextQueryElement.getType() == QueryElementType.ATTRIBUTE || nextQueryElement.getType() == QueryElementType.EXPORTABLE_ATTRIBUTE)
        	{
        		variableNames.add(nextQueryElement.getElement().getName());
        	}
        }
        
        boolean[] isLiteral = new boolean[variableNames.size()];
        
        // TODO: How do we know whether a variable is a link/URI/FK/PK or a string/Literal
        // HACK: Safe, but not as useful, default of true here.
        Arrays.fill(isLiteral, true);
        
        this.out = new RDFXMLOutputStream(out, variableNames, isLiteral, prelude, exception);
    }

    @Override
    public void afterQuery() throws IOException {
        this.out.close();
    }

    private class RDFXMLOutputStream extends FilterOutputStream implements OutputConstants {
        protected boolean startOfLine = true;
        protected boolean exception = false;

        private int uniqueResultIndex = 0;
        private int column = 0;
        private boolean[] isLiteral;
        protected List<String> variableNames;

        public RDFXMLOutputStream(OutputStream out, List<String> variableNames, boolean[] isLiteral, String prelude, String exception) throws IOException 
        {
            super(out);
            
            this.isLiteral = isLiteral;
            this.variableNames = variableNames;
            
            if (exception != null) 
            {
            	// TODO: what will be the standard way of indicating in RDF/XML that there was an error. The exception tag is only available in SPARQL Results XML
                out.write(("<exception>" + exception + "</exception>").getBytes());
                this.exception = true;
                return;
            }

            out.write("<?xml version=\"1.0\"?>\n".getBytes());

            if (prelude != null)
                out.write(prelude.getBytes());

            out.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n".getBytes());
        }
 
        @Override
        public void write(int b) throws IOException 
        {
            if (exception) 
            	return;


            b &= 0xff; // force argument to one byte

            // need to write this once at the start of each line, and we don't get the chance to do this in the Tab-Separated-Values paradigm without this flag
            if (startOfLine) 
            {
                // TODO: remove this hard coded URI prefix when it is available in the configuration or it comes natively as part of a non-write(int i) TSV paradigm
                out.write("<rdf:Description rdf:about=\"http://example.org/test/biomart/rdfxml/result/".getBytes());
                out.write(Integer.toString(uniqueResultIndex++).getBytes());
                out.write("\">\n".getBytes());
                
                startOfLine = false;
            }
            
            StringBuffer buffer = new StringBuffer();
            
            switch(b) 
            {
            	// End of a results set, where the last binding has not yet been written, then start of a results set
                case NEWLINE:
                	// write out the last property
                	writeProperty(column, buffer);
                	
                	// close the last results set
                	out.write("\n</rdf:Description>\n".getBytes());
                	
                    // can't write the next lines introduction here since we don't know if this is the last line or not, so set startOfLine flag for later
                    startOfLine = true;
                    column = 0;
                    break;
                // End of a binding
                case TAB:
                	// write out the next property and value
                	writeProperty(column, buffer);
                	
                	// shift to the next column
                	column++;
                    break;
                // Part of a value
                default:
                	// need to keep these bytes in the buffer so that we can properly encode them if they are multi-byte, and if they are either URI or Literal
                	buffer.append(b);
                    break;
            }
        }

		/**
		 * @param nextColumn The column index for the property
		 * @param propertyValueBuffer The buffer containing the property value
		 * @throws IOException
		 */
		private void writeProperty(int nextColumn, StringBuffer propertyValueBuffer)
				throws IOException 
		{
			// open the element definition
			out.write("<".getBytes());
			out.write(variableNames.get(nextColumn).getBytes());
			// TODO: remove this hard coded value when it is available in the configuration
			out.write(" xmlns=\"http://example.org/test/biomart/rdfxml/attribute\"".getBytes());
			
			if(isLiteral[nextColumn])
			{
				out.write(">".getBytes());
				// TODO: should XML-Encode this string if it contains XML special characters
				out.write(propertyValueBuffer.toString().getBytes());
			}
			// construct the resource URI
			else
			{
				// TODO: remove this hard coded value when it is available in the configuration or is natively given as part of the write(int i) routine or some other output mechanism
				out.write(" rdf:resource=\"http://example.org/test/biomart/rdfxml/objecturi/".getBytes());
				
				// TODO: should URL-PercentEncode this string and XML-Encode it if it contains XML special characters
				out.write(propertyValueBuffer.toString().getBytes());

				out.write("\">".getBytes());
			}
			
			// close the element
			out.write("</".getBytes());
			out.write(variableNames.get(nextColumn).getBytes());
			out.write(">".getBytes());
		}

        @Override
        public void close() throws IOException 
        {
            if (exception) 
            	return;

            out.write("\t</rdf:RDF>\n".getBytes());
            
            super.close();
        }
    }
}
