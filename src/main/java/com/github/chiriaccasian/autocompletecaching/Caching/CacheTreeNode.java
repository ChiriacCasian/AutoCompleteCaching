package com.github.chiriaccasian.autocompletecaching.Caching;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CacheTreeNode {
    private char val;
    private double weight;
    private List<CacheTreeNode> children;
    private CacheTreeNode parent;
    private String suggestion;

    /**
     * Constructs a CacheTreeNode
     *
     * @param character  the character value
     * @param weight     the weight
     * @param suggestion the suggestion string
     * @param parent     the parent node
     */
    public CacheTreeNode(char character, double weight, String suggestion, CacheTreeNode parent) {
        this.val = character;
        this.weight = weight;
        this.suggestion = suggestion;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    /**
     * Constructs a CacheTreeNode by copying another node.
     *
     * @param node the node to copy
     */
    public CacheTreeNode(CacheTreeNode node) {
        this.val = node.getVal();
        this.weight = node.getWeight();
        this.children = node.getChildren();
        this.parent = node.getParent();
        this.suggestion = node.getSuggestion();
    }

    /**
     * Adds a child node to this node
     *
     * @param node the child node
     * @throws Exception if a child with the same value already exists
     */
    public void addChild(CacheTreeNode node) throws Exception {
        for (CacheTreeNode child : children) {
            if (child.getVal() == node.getVal()) {
                throw new Exception("Child with val " + node.getVal() + " already exists in parent");
            }
        }
        children.add(node);
    }

    /**
     * Retrieves a child node by its character value
     *
     * @param character the character value of the child node
     * @return the child node, or null if not found
     */
    public CacheTreeNode getChildByKey(Character character) {
        for (CacheTreeNode child : children) {
            if (child.getVal() == character) return child;
        }
        return null;
    }

    /**
     * Visualizes the graph starting from this node in a less cluttered graph like manner
     *
     * @param prefix the prefix string for recurrence (for initial call it can be "")
     * @return the visualized graph as a string
     */
    public String visualizeGraph(String prefix) {
        StringBuilder rez = new StringBuilder();
        prefix += this.val;
        String whPrefix = prefix.replaceAll(".", " ");
        boolean flag = true;
        for (CacheTreeNode node : children) {
            if (flag) {
                rez.append(node.visualizeGraph(prefix));
                flag = false;
            } else {
                rez.append(node.visualizeGraph(whPrefix));
            }
        }
        if (this.children.size() == 0) return prefix + "\n";
        return rez.toString();
    }

    /**
     * Visualizes all strings in the graph starting from this node
     *
     * @param prefix the prefix string for recurrence (initial can be "")
     * @return the visualized graph as a string
     */
    public String visualizeAllStringsInGraph(String prefix) {
        StringBuilder rez = new StringBuilder();
        prefix += this.val;
        for (CacheTreeNode node : children) {
            rez.append(node.visualizeAllStringsInGraph(prefix));
        }
        if (this.children.size() == 0) return prefix + "\n";
        return rez.toString();
    }

    /**
     * Calculates the size of the children nodes in bytes
     *
     * @return the size of the children nodes
     */
    public int getChildrenSize() {
        int rez = ((suggestion != null) ? suggestion.length() : 0) + 9; // double has 8 bytes
        for (CacheTreeNode node : children) {
            rez += node.getChildrenSize();
        }
        return rez;
    }

    public char getVal() {
        return val;
    }

    public void setVal(char val) {
        this.val = val;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<CacheTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<CacheTreeNode> children) {
        this.children = children;
    }

    public CacheTreeNode getParent() {
        return parent;
    }

    public void setParent(CacheTreeNode parent) {
        this.parent = parent;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return "CacheTreeNode{" +
                "val=" + val +
                ", weight=" + weight +
                ", children=" + children +
                ", parent=" + parent +
                ", suggestion='" + suggestion + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(val, weight, children, parent, suggestion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheTreeNode that = (CacheTreeNode) o;
        return val == that.val &&
                Double.compare(that.weight, weight) == 0 &&
                Objects.equals(children, that.children) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(suggestion, that.suggestion);
    }
}