package com.jsdroid.uiautomator2.event;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jsdroid.uiautomator2.UiDevice;
import com.jsdroid.uiautomator2.UiObject2;

public class NodeEvent {
    public EventType type;
    public AccessibilityEvent accessibilityEvent;
    public UiObject2 node;

    public NodeEvent(AccessibilityEvent event) {
        accessibilityEvent = event;
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            node = new UiObject2(UiDevice.getInstance(), source);
        }
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                type = EventType.TYPE_VIEW_CLICKED;
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                type = EventType.TYPE_VIEW_LONG_CLICKED;
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                type = EventType.TYPE_VIEW_SELECTED;
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                type = EventType.TYPE_VIEW_FOCUSED;
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                type = EventType.TYPE_VIEW_TEXT_CHANGED;
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                type = EventType.TYPE_WINDOW_STATE_CHANGED;
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                type = EventType.TYPE_NOTIFICATION_STATE_CHANGED;
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                type = EventType.TYPE_VIEW_HOVER_ENTER;
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                type = EventType.TYPE_VIEW_HOVER_EXIT;
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                type = EventType.TYPE_TOUCH_EXPLORATION_GESTURE_START;
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                type = EventType.TYPE_TOUCH_EXPLORATION_GESTURE_END;
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                type = EventType.TYPE_WINDOW_CONTENT_CHANGED;
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                type = EventType.TYPE_VIEW_SCROLLED;
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                type = EventType.TYPE_VIEW_TEXT_SELECTION_CHANGED;
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                type = EventType.TYPE_ANNOUNCEMENT;
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                type = EventType.TYPE_VIEW_ACCESSIBILITY_FOCUSED;
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                type = EventType.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED;
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                type = EventType.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY;
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                type = EventType.TYPE_GESTURE_DETECTION_START;
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                type = EventType.TYPE_GESTURE_DETECTION_END;
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                type = EventType.TYPE_TOUCH_INTERACTION_START;
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                type = EventType.TYPE_TOUCH_INTERACTION_END;
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                type = EventType.TYPE_WINDOWS_CHANGED;
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                type = EventType.TYPE_VIEW_CONTEXT_CLICKED;
                break;
            case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT:
                type = EventType.TYPE_ASSIST_READING_CONTEXT;
                break;
        }
    }
}
