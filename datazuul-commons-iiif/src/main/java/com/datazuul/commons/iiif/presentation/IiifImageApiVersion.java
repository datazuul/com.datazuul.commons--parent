package com.datazuul.commons.iiif.presentation;

public enum IiifImageApiVersion {
    V1_1, V2, V3;

    public static IiifImageApiVersion getVersion(String context) {
        if (context == null) {
            return null;
        }

        switch (context) {
            case "http://library.stanford.edu/iiif/image-api/1.1/context.json", "http://iiif.io/api/image/1/context.json":
                return V1_1;
            case "http://iiif.io/api/image/2/context.json":
                return V2;
            case "http://iiif.io/api/image/3/context.json":
                return V3;
            default:
                return null;
        }
    }
}
