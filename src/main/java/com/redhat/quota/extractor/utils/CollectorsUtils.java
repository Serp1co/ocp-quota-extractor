package com.redhat.quota.extractor.utils;

import io.fabric8.kubernetes.api.model.Quantity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Function;

public interface CollectorsUtils {

    static BigDecimal fromKibToMib(BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1024), RoundingMode.DOWN);
    }

    static BigDecimal getNumericalAmountOrNull(Map<String, Quantity> quantityMap, String key) {
        return quantityMap.get(key) != null ? quantityMap.get(key).getNumericalAmount() : null;
    }

    static BigDecimal getNumericalAmountOrNull(Map<String, Quantity> quantityMap,
                                               String key,
                                               Function<BigDecimal, BigDecimal> conversionFunction) {
        return quantityMap.get(key) != null ? conversionFunction.apply(quantityMap.get(key).getNumericalAmount()) : null;
    }

}
