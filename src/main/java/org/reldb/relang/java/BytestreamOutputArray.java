/*
 * BytestreamOutputArray.java
 *
 * Created on 25 April 2004, 02:25
 */

package org.reldb.relang.java;

/**
 * A BytestreamOutput backed by an array of bytes.
 */
public class BytestreamOutputArray extends BytestreamOutput {

    private final static int minimumCapacity = 1024;
    private byte[] bytes = new byte[minimumCapacity];
    private int index = 0;
    
    public void reset() {
        index = 0;
    }
    
    /** Get the array of bytes that represents the stream. */
    public byte[] getBytes() {
        var outArray = new byte[index];
        System.arraycopy(bytes, 0, outArray, 0, index);
        return outArray;
    }
   
    public void put(int aByte) {
        if (index + 1 > bytes.length) {
            var newCapacity = (bytes.length + 1) * 2;
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            } else if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            var newValue = new byte[newCapacity];
            System.arraycopy(bytes, 0, newValue, 0, index);
            bytes = newValue;
        }
        bytes[index++] = (byte)aByte;
    }
    
}
