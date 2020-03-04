package com.jsdroid.sdk.nodes;

import android.view.accessibility.AccessibilityNodeInfo;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Nodes {


    private static class Single {
        private static Nodes instance = new Nodes();
    }

    public static Nodes getInstance() {
        return Single.instance;
    }

    private Nodes() {
    }

    public List<Node> getRootNodes() {
        List<Node> result = new ArrayList<>();
        List<AccessibilityNodeInfo> rootNodes = UiAutomationService.getInstance().getRootNodes();
        if (rootNodes != null) {
            for (AccessibilityNodeInfo rootNode : rootNodes) {
                Node node = new Node(rootNode);
                result.add(node);
            }
        }
        return result;
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

}
