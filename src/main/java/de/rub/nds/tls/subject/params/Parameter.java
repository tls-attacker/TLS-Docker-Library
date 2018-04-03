package de.rub.nds.tls.subject.params;

import java.io.Serializable;

public class Parameter implements Serializable {

    private String cmdParameter;

    private ParameterType type;

    public Parameter() {
    }

    public Parameter(String cmdParameter, ParameterType type) {
        this.cmdParameter = cmdParameter;
        this.type = type;
    }

    public String getCmdParameter() {
        return cmdParameter;
    }

    public void setCmdParameter(String cmdParameter) {
        this.cmdParameter = cmdParameter;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Parameter{" + "cmdParameter=" + cmdParameter + ", type=" + type + '}';
    }
}
