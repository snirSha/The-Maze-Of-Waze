// 
// Decompiled by Procyon v0.5.36
// 

package dataStructure;

import oop_utils.OOP_Point3D;

public interface node_data
{
    int getKey();
    
    OOP_Point3D getLocation();
    
    void setLocation(final OOP_Point3D p0);
    
    double getWeight();
    
    void setWeight(final double p0);
    
    String getInfo();
    
    void setInfo(final String p0);
    
    int getTag();
    
    void setTag(final int p0);
}
