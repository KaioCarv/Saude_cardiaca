package com.saudecardiaca.dto.response;

import java.util.List;

public class HeartHealthRecordListResponse {

    private List<HeartHealthRecordResponse> items;

    public HeartHealthRecordListResponse() {}

    public HeartHealthRecordListResponse(List<HeartHealthRecordResponse> items) {
        this.items = items;
    }

    public List<HeartHealthRecordResponse> getItems() { return items; }
    public void setItems(List<HeartHealthRecordResponse> items) { this.items = items; }
}
