package com.dama.log;

import android.content.Context;
import android.content.Intent;

/**	This class facilitates logging of IME-specific event by the TEMA application.
 *
 *	http://www.cse.yorku.ca/~stevenc/tema/
 *
 *  @version 1.0 - 05/2014
 *  @author Steven J. Castellucci
 */
public class TemaImeLogger
{
    /**	The character indicating a comment line. */
    public final String COMMENT = "[#]";

    /**	The character delimiting the fields. */
    public final String DELIM = "\t";

    /**	The key used to retrieve the String being written to the log. */
    public final String KEY_1 = "data1";

    /**	The key used to retrieve the isComment boolean flag. */
    public final String KEY_2 = "data2";

    /**	The key used to retrieve the timestamp for this event. */
    public final String KEY_3 = "data3";

    private final String BROADCAST_TEMA = "ca.yorku.cse.tema";

    private Context context;


    /**	Initializes this object. */
    public TemaImeLogger(Context c)
    {
        context = c;
    }


    /**	Writes the passed String to TEMA's IME log.
     * 	The string can contain multiple fields or represent a comment.
     * 	Non-comments will be prefixed with a timestamp.
     *
     *	@param s the String to write
     *	@param isComment if true, prefixes the <code>COMMENT</code> String
     */
    public void writeToLog(String s, boolean isComment)
    {
        Intent i = new Intent(BROADCAST_TEMA);
        i.putExtra(KEY_1, s);
        i.putExtra(KEY_2, isComment);
        i.putExtra(KEY_3, System.currentTimeMillis());
        context.sendBroadcast(i);
    }
}
