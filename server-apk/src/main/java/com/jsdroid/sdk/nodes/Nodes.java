package com.jsdroid.sdk.nodes;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jsdroid.api.annotations.MethodDoc;
import com.jsdroid.script.JsDroidScript;

import org.json.JSONArray;
import org.json.JSONException;

import groovy.lang.Closure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Nodes {


    private static class Single {
        private static Nodes instance = new Nodes();
    }

    public static Nodes getInstance() {
        return Single.instance;
    }

    private Nodes() {
        init();
    }

    private void init() {
        setFetchNotImportantNodeEnable(true);
        setFetchWebNodeEnable(true);
    }

    public UiAutomation getUiAutomation() {
        try {
            Field uiAutomationField = UiAutomationService.class.getDeclaredField("uiAutomation");
            uiAutomationField.setAccessible(true);
            return (UiAutomation) uiAutomationField.get(UiAutomationService.getInstance());
        } catch (Exception e) {
        }
        return null;
    }

    public void setFetchNotImportantNodeEnable(boolean enable) {
        if (enable) {
            addNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        } else {
            removeNodeFlag(AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        }
    }

    public void setFetchWebNodeEnable(boolean enable) {
        if (enable) {
            addNodeFlag(AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY);
        } else {
            removeNodeFlag(AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY);
        }
    }

    /**
     * @param flag
     * @see AccessibilityServiceInfo#FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
     * @see AccessibilityServiceInfo#FLAG_REQUEST_TOUCH_EXPLORATION_MODE
     * @see AccessibilityServiceInfo#FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
     * @see AccessibilityServiceInfo#FLAG_REQUEST_FILTER_KEY_EVENTS
     * @see AccessibilityServiceInfo#FLAG_REPORT_VIEW_IDS
     * @see AccessibilityServiceInfo#FLAG_RETRIEVE_INTERACTIVE_WINDOWS
     * @see AccessibilityServiceInfo#FLAG_ENABLE_ACCESSIBILITY_VOLUME
     * @see AccessibilityServiceInfo#FLAG_REQUEST_ACCESSIBILITY_BUTTON
     */
    public void addNodeFlag(int flag) {
        try {
            UiAutomation uiAutomation = getUiAutomation();
            assert uiAutomation != null;
            AccessibilityServiceInfo serviceInfo = uiAutomation.getServiceInfo();
            serviceInfo.flags |= flag;
            uiAutomation.setServiceInfo(serviceInfo);
        } catch (Throwable e) {
        }
    }

    public void removeNodeFlag(int flag) {
        try {
            UiAutomation uiAutomation = getUiAutomation();
            assert uiAutomation != null;
            AccessibilityServiceInfo serviceInfo = uiAutomation.getServiceInfo();
            serviceInfo.flags &= ~flag;
            uiAutomation.setServiceInfo(serviceInfo);
        } catch (Throwable e) {
        }
    }

    public List<Node> getRootNodes() {

        List<Node> result = new ArrayList<>();
        try {
            //初始化失败
            UiAutomationService.getInstance().init();
        } catch (Throwable e) {
            return result;
        }
        List<AccessibilityNodeInfo> rootNodes = UiAutomationService.getInstance().getRootNodes();
        if (rootNodes != null) {
            for (AccessibilityNodeInfo rootNode : rootNodes) {
                Node node = new Node(rootNode);
                result.add(node);
            }
        }
        return result;
    }

    public String getNodeJson() {
        List<Node> rootNodes = getRootNodes();
        JSONArray jsonArray = new JSONArray();
        if (rootNodes != null) {
            for (Node rootNode : rootNodes) {
                try {
                    jsonArray.put(rootNode.getJson());
                } catch (JSONException e) {
                }
            }
        }
        return jsonArray.toString();
    }

    public void eachNode(Node.NodeEach nodeEach) {
        List<Node> rootNodes = getRootNodes();
        for (Node rootNode : rootNodes) {
            if (rootNode.eachNode(nodeEach)) {
                break;
            }
        }
    }

    public void eachNode(Closure each) {
        List<Node> rootNodes = getRootNodes();
        if (rootNodes != null) {
            for (Node rootNode : rootNodes) {
                if (rootNode.eachNode(each)) {
                    break;
                }
            }
        }
    }

    public NodeSearch text(String text) {
        return new NodeSearch().text(text);
    }

    public NodeSearch text(Pattern text) {
        return new NodeSearch().text(text);
    }

    public NodeSearch desc(String desc) {
        return new NodeSearch().desc(desc);
    }

    public NodeSearch desc(Pattern desc) {
        return new NodeSearch().desc(desc);
    }

    public NodeSearch clazz(String clazz) {
        return new NodeSearch().clazz(clazz);
    }

    public NodeSearch clazz(Pattern clazz) {
        return new NodeSearch().clazz(clazz);
    }

    public NodeSearch pkg(String pkg) {
        return new NodeSearch().pkg(pkg);
    }

    public NodeSearch pkg(Pattern pkg) {
        return new NodeSearch().pkg(pkg);
    }

    public NodeSearch res(String res) {
        return new NodeSearch().res(res);
    }

    public NodeSearch res(Pattern res) {
        return new NodeSearch().res(res);
    }

    public NodeSearch index(int index) {
        return new NodeSearch().index(index);
    }

    public NodeSearch depth(int depth) {
        return new NodeSearch().depth(depth);
    }

    public NodeSearch map(Map map) {
        NodeSearch ret = new NodeSearch();
        if (map.containsKey("res")) {
            Object res = map.get("res");
            if (res instanceof Pattern) {
                ret.res((Pattern) res);
            } else {
                ret.res(res.toString());
            }
        }
        if (map.containsKey("text")) {
            Object data = map.get("text");
            if (data instanceof Pattern) {
                ret.text((Pattern) data);
            } else {
                ret.text(data.toString());
            }
        }
        if (map.containsKey("clazz")) {
            Object data = map.get("clazz");
            if (data instanceof Pattern) {
                ret.clazz((Pattern) data);
            } else {
                ret.clazz(data.toString());
            }
        }
        if (map.containsKey("desc")) {
            Object data = map.get("desc");
            if (data instanceof Pattern) {
                ret.desc((Pattern) data);
            } else {
                ret.desc(data.toString());
            }
        }
        if (map.containsKey("pkg")) {
            Object data = map.get("pkg");
            if (data instanceof Pattern) {
                ret.pkg((Pattern) data);
            } else {
                ret.pkg(data.toString());
            }
        }

        if (map.containsKey("depth")) {
            ret.depth(Integer.parseInt((map.get("depth").toString())));
        }
        if (map.containsKey("index")) {
            ret.index(Integer.parseInt((map.get("index").toString())));
        }


        return ret;
    }
}
