// 
// Decompiled by Procyon v0.5.36
// 

package dataStructure;

import java.io.Serializable;
import dataStructure.edge_data;

public class Edge implements edge_data, Serializable
{
    private int _src;
    private int _dest;
    private String _key;
    private double _weight;
    private String _msg;
    private int _tag;
    private double _cap;
    public static double DEFAULT_WEIGHT;
    
    static {
        Edge.DEFAULT_WEIGHT = 1.0;
    }
    
    public Edge(final int src, final int dest) {
        this(src, dest, Edge.DEFAULT_WEIGHT);
    }
    
    public Edge(final int src, final int dest, final double w) {
        this.set_src(src);
        this.set_dest(dest);
        this.set_weight(w);
    }
    
    public edge_data init(final int src, final int dest, final double w) {
        return new Edge(src, dest, w);
    }
    
    public static String makeKey(final int a, final int b) {
        return a + "," + b;
    }
    
    public String getKey() {
        if (this._key == null) {
            this._key = makeKey(this._src, this._dest);
        }
        return this._key;
    }
    
    @Override
    public String toString() {
        String ans = "";
        ans = String.valueOf(ans) + "e(" + this._src + "," + this._dest + "), w:" + this.getWeight() + ",extra p: " + this.getTag() + "," + this.getCapacity() + "," + this.getInfo();
        return ans;
    }
    
    public String toJSON() {
        String ans = "";
        ans = String.valueOf(ans) + "{src:" + this._src + ",dest:" + this._dest + ",weight:" + this.getWeight() + "}";
        return ans;
    }
    
    @Override
    public int getSrc() {
        return this._src;
    }
    
    @Override
    public int getDest() {
        return this._dest;
    }
    
    @Override
    public double getWeight() {
        return this._weight;
    }
    
    public void setCapacity(final double cp) {
        this._cap = cp;
    }
    
    public double getCapacity() {
        return this._cap;
    }
    
    @Override
    public String getInfo() {
        return this._msg;
    }
    
    @Override
    public void setInfo(final String s) {
        this._msg = s;
    }
    
    @Override
    public int getTag() {
        return this._tag;
    }
    
    @Override
    public void setTag(final int t) {
        this._tag = t;
    }
    
    private void set_dest(final int dest) {
        this._dest = dest;
    }
    
    private void set_weight(final double _weight) {
        this._weight = _weight;
    }
    
    private void set_src(final int src) {
        this._src = src;
    }
}
