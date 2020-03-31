package com.jsdroid.sdk.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NodeSearch {
    private String text;
    private Pattern textPattern;
    private String desc;
    private Pattern descPattern;
    private String clazz;
    private Pattern clazzPattern;
    private String pkg;
    private Pattern pkgPattern;
    private String res;
    private Pattern resPattern;
    private Integer index;
    private Integer depth;

    public NodeSearch text(String text) {
        this.text = text;
        return this;
    }

    public NodeSearch text(Pattern text) {
        this.textPattern = text;
        return this;
    }

    public NodeSearch desc(String desc) {
        this.desc = desc;
        return this;
    }

    public NodeSearch desc(Pattern desc) {
        this.descPattern = desc;
        return this;
    }

    public NodeSearch clazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public NodeSearch clazz(Pattern clazz) {
        this.clazzPattern = clazz;
        return this;
    }

    public NodeSearch pkg(String pkg) {
        this.pkg = pkg;
        return this;
    }

    public NodeSearch pkg(Pattern pkg) {
        this.pkgPattern = pkg;
        return this;
    }

    public NodeSearch res(String res) {
        this.res = res;
        return this;
    }

    public NodeSearch res(Pattern res) {
        this.resPattern = res;
        return this;
    }

    public NodeSearch index(int index) {
        this.index = index;
        return this;
    }

    public NodeSearch depth(int depth) {
        this.depth = depth;
        return this;
    }

    private boolean notMatches(Pattern pattern, String beMatch) {
        if (beMatch == null) {
            return true;
        }
        return !pattern.matcher(beMatch).matches();
    }

    public boolean compare(Node node) {
        if (index != null) {
            if (!(index == node.getIndex())) {
                return false;
            }
        }
        if (depth != null) {
            if (!(depth == node.getDepth())) {
                return false;
            }
        }
        if (text != null) {
            if (!this.text.equals(node.getText())) {
                return false;
            }
        }
        if (textPattern != null) {
            if (notMatches(textPattern, node.getText())) {
                return false;
            }
        }
        if (desc != null) {
            if (!desc.equals(node.getDesc())) {
                return false;
            }
        }
        if (descPattern != null) {
            if (notMatches(descPattern, node.getDesc())) {
                return false;
            }
        }
        if (pkg != null) {
            if (!pkg.equals(node.getPkg())) {
                return false;
            }
        }
        if (pkgPattern != null) {
            if (notMatches(pkgPattern, node.getPkg())) {
                return false;
            }
        }
        if (res != null) {
            if (!res.equals(node.getRes())) {
                return false;
            }
        }
        if (resPattern != null) {
            if (notMatches(resPattern, node.getRes())) {
                return false;
            }
        }
        if (clazz != null) {
            if (!clazz.equals(node.getClazz())) {
                return false;
            }
        }
        if (clazzPattern != null) {
            if (notMatches(clazzPattern, node.getClazz())) {
                return false;
            }
        }

        return true;
    }

    public Node findOne() {
        final Store<Node> store = new Store<>();
        Nodes.getInstance().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                if (compare(node)) {
                    store.set(node);
                    return true;
                }
                return false;
            }
        });
        return store.get();
    }

    public List<Node> findAll() {
        final List<Node> result = new ArrayList<>();
        Nodes.getInstance().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                if (compare(node)) {
                    result.add(node);
                }
                return false;
            }
        });
        return result;
    }

    public boolean click() {
        Node one = findOne();
        if (one != null) {
            one.click();
            return true;
        }
        return false;

    }

    public boolean exists() {
        Node one = findOne();
        if (one != null) {
            return true;
        }
        return false;
    }


}
