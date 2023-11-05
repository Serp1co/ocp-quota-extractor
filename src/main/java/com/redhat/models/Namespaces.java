package com.redhat.models;

import lombok.*;

//@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Namespaces /* extends PanacheEntity */ {
    String namespaceName;
    String cluster;
}
