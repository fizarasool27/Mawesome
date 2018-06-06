package in.xeno.mawesome;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {

    private final static JsonParser parser = new JsonParser();

    public static String str(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return null;
        if (j.has(key) && !j.get(key).isJsonNull())
            return j.get(key).getAsString();
        return null;
    }

    public static String str(JsonObject j, String key, String def) {
        if (j == null || key == null || key.isEmpty())
            return def;
        if (j.has(key) && !j.get(key).isJsonNull())
            return j.get(key).getAsString();
        return def;
    }

    public static String innerStr(JsonObject j, String outer, String inner) {
        if (j == null || outer == null || outer.isEmpty() || inner == null || inner.isEmpty())
            return null;
        if (j.has(outer) && j.get(outer).isJsonObject())
            return Util.str(j.get(outer).getAsJsonObject(), inner);
        return null;
    }


    public static Double num(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return 0.0;
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsDouble();
            } catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public static float numAsFloat(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return 0.0f;
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsFloat();
            } catch (Exception e) {
                return 0.0f;
            }
        }
        return 0.0f;
    }

    public static long numAsLong(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return 0;
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsLong();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static int numAsInt(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return 0;
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsInt();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static JsonObject obj(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return null;
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsJsonObject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static JsonArray arr(JsonObject j, String key) {
        if (j == null || key == null || key.isEmpty())
            return new JsonArray();
        if (j.has(key) && !j.get(key).isJsonNull()) {
            try {
                return j.get(key).getAsJsonArray();
            } catch (Exception e) {
                return new JsonArray();
            }
        }
        return new JsonArray();
    }



    public static JsonObject toObject(String o) {
        if(o == null)
            return null;
        return parser.parse(o).getAsJsonObject();
    }

    public static JsonObject toObject(String o , String key) {
        if(o == null)
            return null;
        JsonObject j = parser.parse(o).getAsJsonObject();
        if(j.has(key))
            return j.get(key).getAsJsonObject();
        return null;
    }

    public static JsonArray toArr(String o) {
        if(o == null)
            return new JsonArray();
        return parser.parse(o).getAsJsonArray();
    }

//    public static Map<String , Object> fromXML(String xml) throws IOException, SAXException,
// ParserConfigurationException {
//        Map<String , Object> map = XMLParser.getMapFromXmlString(xml);
//        JsonObject j = new JsonObject();
//        new Gson().
//        try {
//            return XENO.json.writeValueAsString(o);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
