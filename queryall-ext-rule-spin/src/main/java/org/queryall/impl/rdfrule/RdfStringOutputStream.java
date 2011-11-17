package org.queryall.impl.rdfrule;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Captures output as a UTF-8 character stream that can be serialised to a string as necessary
 */
public class RdfStringOutputStream extends OutputStream
{
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    private StringBuffer stringBuffer = new StringBuffer();
    
    @Override
    public void close()
    {
        this.stringBuffer.setLength(0);
        this.stringBuffer = null;
    }
    
    @Override
    public String toString()
    {
        return this.stringBuffer.toString();
    }
    
    @Override
    public void write(final byte[] byteArray) throws IOException
    {
        if(this.stringBuffer == null)
        {
            throw new IOException("Attempted to write to closed output stream");
        }
        
        this.stringBuffer.append(new String(byteArray, RdfStringOutputStream.UTF_8));
    }
    
    @Override
    public void write(final byte[] byteArray, final int offset, final int length) throws IOException
    {
        if(this.stringBuffer == null)
        {
            throw new IOException("Attempted to write to closed output stream");
        }
        
        this.stringBuffer.append(new String(byteArray, offset, length, RdfStringOutputStream.UTF_8));
    }
    
    @Override
    public void write(final int byteAsInt) throws IOException
    {
        if(this.stringBuffer == null)
        {
            throw new IOException("Attempted to write to closed output stream");
        }
        
        final byte[] singleByte = new byte[1];
        singleByte[0] = (byte)byteAsInt;
        this.stringBuffer.append(new String(singleByte, RdfStringOutputStream.UTF_8));
    }
}