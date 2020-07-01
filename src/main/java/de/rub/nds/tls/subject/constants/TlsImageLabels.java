package de.rub.nds.tls.subject.constants;

public enum TlsImageLabels {
    IMPLEMENTATION("tls_implementation"),
    VERSION("tls_implementation_version"),
    CONNECTION_ROLE("tls_implementation_connectionRole");

    private String labelName;

    TlsImageLabels(String label) {
        this.labelName = label;
    }

    public String getLabelName() {
        return this.labelName;
    }
}
