package com.redhat.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Entity
@Getter
@Setter
@ToString
public class Namespaces /* extends PanacheEntity */ {
    String NamespaceName;
    String Cluster;
}
