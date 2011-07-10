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

import org.apache.log4j.Logger;

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

	public static final Logger log = Logger.getLogger(StaticFileServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

	@Override
    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
        throws ServletException, IOException 
    {
		
		// add /static/ at the start and then we can later on verify that /static/ is still in the canonical URL
    	String filename = "/static/"+(String)request.getAttribute("org.queryall.servlets.StaticFileServlet.filename");
    	
	    if(_DEBUG)
	    {
	    	log.debug("filename="+filename);
	    }
	    
	    File f = null;
		try
		{
			URL fileResource = this.getClass().getResource(filename);
			
			if(fileResource == null)
			{
	    		log.error("Could not find the requested static resource. fileResource was null filename="+filename);
				throw new ServletException("Could not find the requested static resource");
			}
			
	    	f = new File(fileResource.toURI());
	    	
	    	// check to see if /static/ is in the path still
	    	if(f.getCanonicalPath().contains(File.separator+"static"+File.separator))
	    	{	    	
			    int                 length   = 0;
			    ServletOutputStream op       = response.getOutputStream();
			    ServletContext      context  = getServletConfig().getServletContext();
			    String              mimetype = context.getMimeType( filename );
			
			    if(_DEBUG)
			    {
			    	log.debug("this.getClass().getResource(filename).toURI()="+this.getClass().getResource(filename).toURI().toString());
			    	log.debug("filename="+filename+" f.getName()="+f.getName()+" mimetype="+mimetype+" f.length()="+f.length());
			    }
			    
			    //
			    //  Set the response and go!
			    //
			    //
			    response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
			    response.setContentLength( (int)f.length() );
			    response.setHeader( "Content-Disposition", "attachment; filename=\"" + f.getName() + "\"" );
			    // TODO: put in expires and etag
			
			    //
			    //  Stream to the requester.
			    //
			    byte[] bbuf = new byte[BUFSIZE];
			    DataInputStream in = new DataInputStream(new FileInputStream(f));
			
			    while ((in != null) && ((length = in.read(bbuf)) != -1))
			    {
			        op.write(bbuf,0,length);
			    }
			
			    in.close();
			    op.flush();
			    op.close();
	    	}
	    	else
	    	{
	    		log.error("Could not find the requested static resource. f.getCanonicalPath()="+f.getCanonicalPath()+" filename="+filename);
	    		throw new ServletException("Could not find the requested static resource");
	    	}
		}
		catch(URISyntaxException e)
		{
			log.error("Could not find the requested static resource. Found URISyntaxException", e);
    		throw new ServletException("Could not find the requested static resource");
		}
	}
}
