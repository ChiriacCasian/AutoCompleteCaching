package com.github.chiriaccasian.autocompletecaching.Caching;

import java.util.Objects;

public class CacheClient {
    private CacheTreeNode head;

    /**
     * Constructor
     */
    public CacheClient() {
        head = new CacheTreeNode('$', 1, null, null); // head has no character
    }

    /**
     * Visualizes the entire cache tree as a string
     * switch between modes of visualisation here
     *
     * @return the visualized cache tree string
     */
    public String visualizeGraph() {
        return head.visualizeAllStringsInGraph("_");
        //return head.visualizeGraph("_") ;
    }

    /**
     * Retrieves a suggestion based on the given context
     * If the context is not found in the cache, returns null (cache miss)
     *
     * @param context the context string
     * @return the suggestion or null if none is found
     */
    public String getSuggestion(String context){
        CacheTreeNode localHead = new CacheTreeNode(head);
        for (int f = 0; f < context.length(); f++) {
            Character character = context.charAt(f);
            CacheTreeNode nextNode = localHead.getChildByKey(character);
            if (nextNode == null) {
                return null; // context is longer than the currently cached prefix
            } else {
                localHead = nextNode;
            }
        }
        return localHead.getSuggestion();
    }

    /**
     * Caches a suggestion based on the given context
     *
     * @param context    the context string
     * @param suggestion the suggestion to cache
     * @throws Exception if a child with the same value already exists
     */
    public void cacheSuggestion(String context, String suggestion) throws Exception {
        CacheTreeNode localHead = new CacheTreeNode(head);
        for (int f = 0; f < context.length(); f++) {
            Character character = context.charAt(f);
            CacheTreeNode nextNode = localHead.getChildByKey(character);
            if (nextNode == null) {
                CacheTreeNode newNode = new CacheTreeNode(character, 1, suggestion, localHead);
                localHead.addChild(newNode);
                localHead = newNode;
            } else {
                localHead = nextNode;
            }
        }
    }

    /**
     * Clears the cache by resetting the head node
     * !This is a temporary solution and does not actually delete all nodes from memory
     */
    public void clearCache() {
        head = new CacheTreeNode('$', 1, null, null);
    }

    /**
     * Returns the size of the cache in bytes
     *
     * @return the cache size
     */
    public int getCacheSize() {
        return head.getChildrenSize();
    }

    public CacheTreeNode getHead() {
        return head;
    }

    public void setHead(CacheTreeNode head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "CacheClient{" +
                "head=" + head +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(head);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheClient that = (CacheClient) o;
        return Objects.equals(head, that.head);
    }
}