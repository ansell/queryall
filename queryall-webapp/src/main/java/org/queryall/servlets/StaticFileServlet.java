/**
 * 
 */
package org.queryall.servlets;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StaticFileServlet extends HttpServlet
{
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -4462026270078646316L;
    private static final int BUFSIZE = 2048;
    
    public static final Logger log = LoggerFactory.getLogger(StaticFileServlet.class);
    public static final boolean _TRACE = StaticFileServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = StaticFileServlet.log.isDebugEnabled();
    public static final boolean _INFO = StaticFileServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        
        // add /static/ at the start and then we can later on verify that /static/ is still in the
        // canonical URL
        final String filename =
                "/static/" + (String)request.getAttribute("org.queryall.servlets.StaticFileServlet.filename");
        
        if(StaticFileServlet._DEBUG)
        {
            StaticFileServlet.log.debug("filename=" + filename);
        }
        
        URL fileResource = null;
        
        try
        {
            
            fileResource = this.getClass().getResource(filename);
            
            if(fileResource == null)
            {
                StaticFileServlet.log
                        .error("Could not find the requested static resource. fileResource was null filename="
                                + filename);
                throw new ServletException("Could not find the requested static resource");
            }
            else if(_INFO)
            {
                log.info("fileResource.toString()="+fileResource.toString());
                log.info("fileResource.toURI()="+fileResource.toURI());
            }
            
            //f = new File(fileResource.toURI());
            
            // check to see if /static/ is in the path still
            if(fileResource.getPath().contains(File.separator + "static" + File.separator))
            {
                int length = 0;
                final ServletOutputStream op = response.getOutputStream();
                final ServletContext context = this.getServletConfig().getServletContext();
                final String mimetype = context.getMimeType(filename);
                
                if(StaticFileServlet._DEBUG)
                {
                    StaticFileServlet.log.debug("this.getClass().getResource(filename).toURI()="
                            + this.getClass().getResource(filename).toURI().toString());
//                    StaticFileServlet.log.debug("filename=" + filename + " f.getName()=" + f.getName() + " mimetype="
//                            + mimetype + " f.length()=" + f.length());
                }
                
                //
                // Set the response and go!
                //
                //
                response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                //response.setContentLength((int)f.length());
                
                //int lastIndexOf = fileResource.toURI().getPath().lastIndexOf("/");
                
                String parsedFilename = fileResource.getFile();
                
                if(parsedFilename != null && parsedFilename.trim().length() > 0)
                {
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + parsedFilename + "\"");
                }
                else
                {
                    log.error("Could not find a filename for filename="+filename+" parsedFilename="+parsedFilename);
                }
                // TODO: put in expires and etag
                
                //
                // Stream to the requester.
                //
                final byte[] bbuf = new byte[StaticFileServlet.BUFSIZE];
                final DataInputStream in = new DataInputStream(this.getClass().getResourceAsStream(filename));
                
                while((in != null) && ((length = in.read(bbuf)) != -1))
                {
                    op.write(bbuf, 0, length);
                }
                
                in.close();
                op.flush();
                op.close();
            }
            else
            {
                StaticFileServlet.log.error("Could not find the requested static resource. f.getCanonicalPath()="
                        + fileResource.getPath() + " filename=" + filename);
                throw new ServletException("Could not find the requested static resource");
            }
        }
        catch(final URISyntaxException e)
        {
            StaticFileServlet.log.error("Could not find the requested static resource. Found URISyntaxException", e);
            throw new ServletException("Could not find the requested static resource");
        }
    }
}
