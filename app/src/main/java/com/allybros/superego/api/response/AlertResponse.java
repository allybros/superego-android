package com.allybros.superego.api.response;

import com.allybros.superego.unit.Alert;

import java.util.List;

public class AlertResponse {
    private List<Alert> alerts;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }
}
