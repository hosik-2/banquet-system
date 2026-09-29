package com.convention.event_system.query;

import com.convention.event_system.domain.Banquet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BanquetDetail {

    private Banquet banquet;
    private AuditInfo auditInfo;

}
