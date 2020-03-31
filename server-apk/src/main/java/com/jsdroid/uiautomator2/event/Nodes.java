package com.jsdroid.uiautomator2.event;

import android.app.UiAutomation;
import android.view.accessibility.AccessibilityEvent;

import com.jsdroid.uiautomator2.UiDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nodes implements UiAutomation.OnAccessibilityEventListener {
    private static Nodes instance = new Nodes();

    public static Nodes getInstance() {
        return instance;
    }

    private final Map<String, NodeListener> nodeListenerMap;
    private List<Wait> waits;

    private Nodes() {
        nodeListenerMap = new HashMap<>();
        waits = new ArrayList<>();
        UiDevice.getInstance().addAccessibilityEventListener("Nodes", this);
    }

    public void addNodeListener(String name, NodeListener nodeListener) {
        synchronized (nodeListenerMap) {
            nodeListenerMap.put(name, nodeListener);
        }
    }

    public void removeNodeListener(String name) {
        synchronized (nodeListenerMap) {
            nodeListenerMap.remove(name);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        NodeEvent nodeEvent = new NodeEvent(event);
        synchronized (nodeListenerMap) {
            for (NodeListener value : nodeListenerMap.values()) {
                try {
                    value.onEvent(nodeEvent);
                } catch (Exception e) {
                }
            }
        }
    }
}
