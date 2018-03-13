package tlf.com.tlfdebug.tool;

import java.util.LinkedList;

/**
 * Created by ${wq} on 2017/8/26.
 */

public class ShowTool {
    private static LinkedList<String> list = new LinkedList<String>();
    private static LinkedList<String> listTemp = new LinkedList<String>();
    private static StringBuilder sb = new StringBuilder();
    private static StringBuilder sbTemp = new StringBuilder();

    public static String getLinkString(String data) {
        sb.setLength(0);
        list.add(data);
        if (list.size() > 10) {
            list.removeFirst();
        }
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i) + "\n");
        }
        return sb.toString();
    }

    public static String getLinkTempString(String data) {
        sbTemp.setLength(0);
        listTemp.add(data);
        if (listTemp.size() > 10) {
            listTemp.removeFirst();
        }
        for (int i = 0; i < listTemp.size(); i++) {
            sbTemp.append(listTemp.get(i) + "\n");
        }
        return sbTemp.toString();
    }

    public static void clear() {
        list.clear();
        listTemp.clear();
    }
}
