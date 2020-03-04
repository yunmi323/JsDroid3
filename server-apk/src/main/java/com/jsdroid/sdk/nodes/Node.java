package com.jsdroid.sdk.nodes;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.events.Events;
import groovy.lang.Closure;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private AccessibilityNodeInfo nodeInfo;
    private int index;
    private int depth;
    private Node parent;
    private List<Node> children;

    public Node() {
    }

    public Node(AccessibilityNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public boolean isVisible() {
        return nodeInfo.isVisibleToUser();
    }

    public boolean isCheckable() {
        return nodeInfo.isCheckable();
    }


    public boolean isClickable() {
        return nodeInfo.isClickable();
    }

    public boolean isChecked() {
        return nodeInfo.isChecked();
    }


    public boolean isEditable() {
        return nodeInfo.isEditable();
    }


    public boolean isPassword() {
        return nodeInfo.isPassword();
    }

    public boolean isFocusable() {
        return nodeInfo.isFocusable();
    }


    public boolean isFocused() {
        return nodeInfo.isFocused();
    }

    public boolean isScrollable() {
        return nodeInfo.isScrollable();
    }


    public boolean isSelected() {
        return nodeInfo.isSelected();
    }

    //通过快速搜索，或者其他方式创建的节点，需要获取父节点的时候，要保留此参数，用于合并到子节点，增加搜索效率
    private Node fromChild;

    private void fetchParent() {
        AccessibilityNodeInfo parentNodeInfo = findParent(this.nodeInfo);
        if (parentNodeInfo != null) {
            parent = new Node(parentNodeInfo);
            parent.fromChild = this;
            parent.fetchParent();
        } else {
            //根节点，刷新深度信息
            fetchDepth();
        }
    }

    private void fetchDepth() {
        if (fromChild != null) {
            fromChild.depth = depth + 1;
            fromChild.fetchDepth();
        }
    }

    public Node getRootNode() {
        Node parent = getParent();
        if (parent != null) {
            return parent.getRootNode();
        }
        return this;
    }

    public long getNodeId() {
        return NodeHelper.getNodeId(nodeInfo);
    }

    private void fetchChildren() {
        children = new ArrayList<>();
        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //有些界面直接获取子节点会超时，因此由此黑科技方法先刷新一下即可
            AccessibilityNodeInfo child = findChild(nodeInfo, i);
            if (child != null) {
                if (fromChild != null && fromChild.getNodeId() == NodeHelper.getNodeId(child)) {
                    children.add(fromChild);
                    fromChild.parent = this;
                    fromChild.depth = this.depth + 1;
                    fromChild.index = i;
                } else {
                    Node node = new Node(child);
                    children.add(node);
                    node.parent = this;
                    node.depth = this.depth + 1;
                    node.index = i;
                }
            }
        }
    }

    private AccessibilityNodeInfo findChild(AccessibilityNodeInfo nodeInfo, int i) {
        try {
            int id = UiAutomationService.getInstance().getConnectionId();
            return NodeFinder.getInstance().findNode(id, nodeInfo.getWindowId(), NodeHelper.getChildId(nodeInfo, i));
        } catch (Throwable e) {
            return nodeInfo.getChild(i);
        }
    }

    private AccessibilityNodeInfo findParent(AccessibilityNodeInfo nodeInfo) {
        int id = UiAutomationService.getInstance().getConnectionId();
        return NodeFinder.getInstance().findNode(id, nodeInfo.getWindowId(), NodeHelper.getParentId(nodeInfo));
    }

    private String toString(CharSequence text) {
        return text == null ? null : text.toString();
    }

    public AccessibilityNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public int getIndex() {
        return index;
    }


    public int getDepth() {
        return depth;
    }


    public long getWindowId() {
        return nodeInfo.getWindowId();
    }


    public String getDesc() {
        return toString(nodeInfo.getContentDescription());
    }


    public String getPkg() {
        return toString(nodeInfo.getPackageName());
    }


    public String getText() {
        return toString(nodeInfo.getText());
    }

    public String getClazz() {
        return toString(nodeInfo.getClassName());
    }

    public String getRes() {
        return toString(nodeInfo.getViewIdResourceName());
    }

    public Rect getRect() {
        return getVisibleBounds(nodeInfo);
    }

    public Node getParent() {
        if (parent == null) {
            fetchParent();
        }
        return parent;
    }

    public List<Node> getChildren() {
        if (children == null) {
            fetchChildren();
        }
        return children;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("index", index);
        jsonObject.put("depth", depth);
        jsonObject.put("windowId", getWindowId());
        jsonObject.put("desc", getDesc());
        jsonObject.put("pkg", getPkg());
        jsonObject.put("text", getText());
        jsonObject.put("clazz", getClazz());
        jsonObject.put("res", getRes());
        jsonObject.put("checkable", isCheckable());
        jsonObject.put("clickable", isClickable());
        jsonObject.put("checked", isChecked());
        jsonObject.put("editable", isEditable());
        jsonObject.put("password", isPassword());
        jsonObject.put("focusable", isFocusable());
        jsonObject.put("focused", isFocused());
        jsonObject.put("scrollable", isScrollable());
        jsonObject.put("selected", isSelected());
        jsonObject.put("rect", getRectJson());
        if (children == null) {
            fetchChildren();
        }
        if (children != null) {
            JSONArray childrenJson = new JSONArray();
            for (Node child : children) {
                childrenJson.put(child.getJson());
            }
            jsonObject.put("children", childrenJson);
        }
        return jsonObject;
    }

    private JSONObject getRectJson() throws JSONException {
        Rect rect = getRect();
        if (rect != null) {
            JSONObject rectJson = new JSONObject();
            rectJson.put("left", rect.left);
            rectJson.put("right", rect.right);
            rectJson.put("top", rect.top);
            rectJson.put("bottom", rect.bottom);
            return rectJson;
        }
        return new JSONObject();
    }

    public String toJsonString() throws JSONException {
        return getJson().toString();
    }

    //快速搜索文字
    public List<Node> findText(String text) {
        List<Node> ret = new ArrayList<>();
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(text);
        for (AccessibilityNodeInfo info : nodeInfos) {
            ret.add(new Node(info));
        }
        return ret;
    }

    //快速搜索资源
    public List<Node> findRes(String text) {
        List<Node> ret = new ArrayList<>();
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(text);
        for (AccessibilityNodeInfo info : nodeInfos) {
            ret.add(new Node(info));
        }
        return ret;
    }

    private Rect getVisibleBounds(AccessibilityNodeInfo node) {
        // Get the object bounds in screen coordinates
        Rect ret = new Rect();
        node.getBoundsInScreen(ret);
        Rect screen = new Rect(0, 0, Devices.getInstance().getWidth(), Devices.getInstance().getHeight());
        ret.intersect(screen);
        // Find the visible bounds of our first scrollable ancestor
        AccessibilityNodeInfo ancestor;
        for (ancestor = node.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor.isScrollable()) {
                Rect ancestorRect = getVisibleBounds(ancestor);
                ret.intersect(ancestorRect);
                break;
            }
        }
        return ret;
    }

    public void click() {
        Rect rect = getRect();
        Events.getInstance().tap(rect.centerX(), rect.centerY());
    }

    public void longClick() {
        Rect rect = getRect();
        Events.getInstance().longClick(rect.centerX(), rect.centerY());
    }

    public void performAction(int action) {
        nodeInfo.performAction(action);
    }

    public boolean eachNode(Closure each) {
        Object ret = each.call(this);
        if (ret != null) {
            if (ret.equals(true)) {
                return true;
            }
        }
        List<Node> children = getChildren();
        if (children != null) {
            for (Node child : children) {
                boolean end = child.eachNode(each);
                if (end) {
                    return true;
                }

            }
        }
        return false;
    }

    public boolean eachNode(NodeEach nodeEach) {
        if (nodeEach.each(this)) {
            return true;
        }
        List<Node> children = getChildren();
        if (children != null) {
            for (Node child : children) {
                boolean end = child.eachNode(nodeEach);
                if (end) {
                    return true;
                }

            }
        }
        return false;
    }

    public interface NodeEach {
        boolean each(Node node);
    }
}
