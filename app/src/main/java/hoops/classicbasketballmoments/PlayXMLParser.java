package hoops.classicbasketballmoments;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by singerm on 7/22/2016.
 */
public class PlayXMLParser {

    // We don't use namespaces
    private static final String ns = null;
    private static final String TAG = "PlayXMLParser";

    private boolean exitFlag = false;
    String mPlayName = "";
    String mUrl = "";

    public ArrayList<String> getPlay(String mPlayName, int offenseOrDefense, Context mContext) throws IOException, XmlPullParserException {

        InputStream is = mContext.getResources().openRawResource(R.raw.oplays);
        this.mPlayName = mPlayName;
        return parse(is);

    }

    public ArrayList<String> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<String> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> entries = new ArrayList<String>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v(TAG, "in readFeed... name = " + name);
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries = readEntry(parser);
                if (exitFlag)
                    return entries;
            }
            //else if (name.equals("action"))
            //    entries.addAll(readAction(parser));
            else
                skip(parser);
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private ArrayList<String> readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> entry = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v(TAG, "INSIDE readEntry. Var name = " + name);
            if (name.equals("id")) {
                if (readText(parser).equals(mPlayName)) {
                    exitFlag = true;

                    Log.v(TAG, name + " = " + mPlayName);
                    // id tag
                    Log.v(TAG, "1 in readEntry. current xml tag is = " + parser.getName());

                    // null tag
                    parser.next();
                    Log.v(TAG, "2 in readEntry. current xml tag is = " + parser.getName());

                    //action or url tag
                    parser.next();
                    Log.v(TAG, "3 in readEntry. current xml tag is = " + parser.getName());

                    if (parser.getName().equals("url")) {
                        mUrl = readText(parser);
                        Log.v(TAG, "URL = " + mUrl);
                        // null tag
                        parser.next();
                        // action tag
                        parser.next();
                    }

                    Log.v(TAG, "4 in readEntry. current xml tag is = " + parser.getName());
                    return readActions(parser);
                } //else
                    //skip(parser);
            }
            else
                skip(parser);
        }
        return entry;
    }

/*    private ArrayList<String> readAction(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> entry = new ArrayList<>();
        Log.v(TAG, "in readActions. current xml tag is = " + parser.getName());
        parser.require(XmlPullParser.START_TAG, ns, "action");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v(TAG, "in readActions. current xml tag name is = " + name);
            if (name.equals("ball") || name.equals("opg") || name.equals("osg") || name.equals("osf") || name.equals("opf") || name.equals("oc"))
                entry.add(name + "," + read(parser, name));
        }
        return entry;
    }*/

    private boolean isValidAction(String action) {
        switch (action) {
            case "ball":
            case "opg":
            case "dpg":
            case "osg":
            case "dsg":
            case "osf":
            case "dsf":
            case "opf":
            case "dpf":
            case "oc":
            case "dc":
                return true;
            default:
                return false;
        }
    }

    private ArrayList<String> readActions(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> entry = new ArrayList<>();
        Log.v(TAG, "in readActions 1. current xml tag is = " + parser.getName());
        //parser.require(XmlPullParser.START_TAG, ns, "action");
        while (parser.getName().equals("action")) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                Log.v(TAG, "in readActions 2. current xml tag name is = " + name);
                if (isValidAction(name))
                    entry.add(name + "," + read(parser, name));
            }
            // new
            Log.v(TAG, "in readActions 3. current xml tag is = " + parser.getName());
            parser.next();
            parser.next();
            Log.v(TAG, "in readActions 4. current xml tag is = " + parser.getName());
        }
        Log.v(TAG, "exiting readActions while loop. current parser value = " + parser.getName());
        return entry;
    }

    private String read(XmlPullParser parser, String playerPos) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, playerPos);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, playerPos);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            //throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
