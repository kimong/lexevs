/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package edu.mayo.informatics.indexer.utility;

import java.io.File;

/**
 * Small utility type routines used in the indexer.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust </A>
 */
public class Utility {
    public static void removeSubFolders(File folder) {
        File[] temp = folder.listFiles();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].isDirectory()) {
                deleteRecursive(temp[i]);
            }
        }

    }

    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] temp = file.listFiles();
            for (int i = 0; i < temp.length; i++) {
                deleteRecursive(temp[i]);
            }
        }
        file.delete();
    }

    public static String replaceStringsInString(StringBuffer input, String searchFor, String replaceWith) {
        // int loc = input.indexOf(searchFor);//jdk 1.3 compliance... grrr
        int loc = input.toString().indexOf(searchFor);
        while (loc != -1) {
            input.replace(loc, loc + searchFor.length(), replaceWith);
            // loc = input.indexOf(searchFor);
            loc = input.toString().indexOf(searchFor);
        }
        return input.toString();
    }

    public static String padStringBuffer(StringBuffer input, char padChar, int desiredLength, boolean prepend) {
        if (prepend) {

            while (input.length() < desiredLength) {
                input.insert(0, padChar);
            }
        } else {
            while (input.length() < desiredLength) {
                input.append(padChar);
            }
        }
        return input.toString();

    }

    public static String padString(String input, char padChar, int desiredLength, boolean prepend) {
        return padStringBuffer(new StringBuffer(input), padChar, desiredLength, prepend);
    }

    public static String padInt(int input, char padChar, int desiredLength, boolean prepend) {
        return padStringBuffer(new StringBuffer(input + ""), padChar, desiredLength, prepend);
    }

}