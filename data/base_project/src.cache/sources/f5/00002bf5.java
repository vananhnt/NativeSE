package org.json;

import java.util.Iterator;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JSONObject.class */
public class JSONObject {
    public static final Object NULL = null;

    public JSONObject() {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(Map copyFrom) {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(JSONTokener readFrom) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(String json) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject(JSONObject copyFrom, String[] names) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public int length() {
        throw new RuntimeException("Stub!");
    }

    public JSONObject put(String name, boolean value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject put(String name, double value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject put(String name, int value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject put(String name, long value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject put(String name, Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject putOpt(String name, Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject accumulate(String name, Object value) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public Object remove(String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNull(String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean has(String name) {
        throw new RuntimeException("Stub!");
    }

    public Object get(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public Object opt(String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean getBoolean(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public boolean optBoolean(String name) {
        throw new RuntimeException("Stub!");
    }

    public boolean optBoolean(String name, boolean fallback) {
        throw new RuntimeException("Stub!");
    }

    public double getDouble(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public double optDouble(String name) {
        throw new RuntimeException("Stub!");
    }

    public double optDouble(String name, double fallback) {
        throw new RuntimeException("Stub!");
    }

    public int getInt(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public int optInt(String name) {
        throw new RuntimeException("Stub!");
    }

    public int optInt(String name, int fallback) {
        throw new RuntimeException("Stub!");
    }

    public long getLong(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public long optLong(String name) {
        throw new RuntimeException("Stub!");
    }

    public long optLong(String name, long fallback) {
        throw new RuntimeException("Stub!");
    }

    public String getString(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public String optString(String name) {
        throw new RuntimeException("Stub!");
    }

    public String optString(String name, String fallback) {
        throw new RuntimeException("Stub!");
    }

    public JSONArray getJSONArray(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONArray optJSONArray(String name) {
        throw new RuntimeException("Stub!");
    }

    public JSONObject getJSONObject(String name) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public JSONObject optJSONObject(String name) {
        throw new RuntimeException("Stub!");
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public Iterator keys() {
        throw new RuntimeException("Stub!");
    }

    public JSONArray names() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public String toString(int indentSpaces) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public static String numberToString(Number number) throws JSONException {
        throw new RuntimeException("Stub!");
    }

    public static String quote(String data) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.json.JSONObject$1  reason: invalid class name */
    /* loaded from: JSONObject$1.class */
    static class AnonymousClass1 {
        AnonymousClass1() {
        }

        public boolean equals(Object o) {
            return o == this || o == null;
        }

        public String toString() {
            return "null";
        }
    }
}