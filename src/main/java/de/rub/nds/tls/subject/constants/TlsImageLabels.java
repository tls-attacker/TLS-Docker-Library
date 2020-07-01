package de.rub.nds.tls.subject.constants;

public enum TlsImageLabels {
    LIBRARY("tls_library"),
    VERSION("tls_library_version"),
    MODE("tls_library_mode");

    private String labelName;

    TlsImageLabels(String label) {
        this.labelName = label;
    }

    public String getLabelName() {
        return this.labelName;
    }
}
