package com.redhat.quota.extractor.collectors;

import io.fabric8.openshift.client.OpenShiftClient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.stream.Stream;

public interface ICollector<T> {

    Stream<T> collect(OpenShiftClient openShiftClient, String... namespaces);

    @Getter
    @Setter
    @ToString
    class Tuple<K, V> {

        private K first;
        private V second;

        public Tuple(K first, V second) {
            this.first = first;
            this.second = second;
        }

    }

}
